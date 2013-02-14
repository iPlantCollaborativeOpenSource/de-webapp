/**
 * 
 */
package org.iplantc.de.client.desktop.views;

import java.util.List;

import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.models.WindowState;
import org.iplantc.core.uicommons.client.widgets.IPlantAnchor;
import org.iplantc.core.uicommons.client.widgets.PushButton;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.collaborators.views.ManageCollaboratorsDailog;
import org.iplantc.de.client.desktop.widget.Desktop;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.events.NotificationCountUpdateEvent.NotificationCountUpdateEventHandler;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.images.Resources;
import org.iplantc.de.client.preferences.views.PreferencesDialog;
import org.iplantc.de.client.utils.WindowUtil;
import org.iplantc.de.client.views.panels.ViewNotificationMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * Default DE View as Desktop
 * 
 * FIXME JDS Move more UI construction into ui.xml
 * 
 * @author sriram
 * 
 */
public class DEViewImpl implements DEView {

    private static DEViewUiBinder uiBinder = GWT.create(DEViewUiBinder.class);

    @UiField
    HorizontalLayoutContainer headerPanel;
    @UiField
    SimpleContainer mainPanel;

    @UiField
    MarginData centerData;
    @UiField
    BorderLayoutContainer con;

    private NotificationIndicator lblNotifications;
    private ViewNotificationMenu notificationsView;

    private final Widget widget;

    private final DeResources resources;
    private final EventBus eventBus;

	private DEView.Presenter presenter;
    private final Desktop desktop;

    @UiTemplate("DEView.ui.xml")
    interface DEViewUiBinder extends UiBinder<Widget, DEViewImpl> {
    }

    public DEViewImpl(final DeResources resources, final EventBus eventBus) {
        this.resources = resources;
        this.eventBus = eventBus;
        widget = uiBinder.createAndBindUi(this);

        desktop = new Desktop(resources, eventBus);
        con.remove(con.getCenterWidget());
        con.setCenterWidget(desktop, centerData);
        
        con.setStyleName(resources.css().iplantcBackground());
        initEventHandlers();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    private void initEventHandlers() {
        EventBus eventbus = EventBus.getInstance();

        // handle data events
        eventbus.addHandler(NotificationCountUpdateEvent.TYPE,
                new NotificationCountUpdateEventHandler() {

                    @Override
                    public void onCountUpdate(NotificationCountUpdateEvent ncue) {
                        int new_count = ncue.getTotal();
                        if (new_count > 0 && new_count > lblNotifications.getCount()) {
                            notificationsView.fetchUnseenNotifications();
                        }
                        notificationsView.setUnseenCount(new_count);
                        lblNotifications.setCount(new_count);

                    }
                });
    }

    @Override
    public void drawHeader() {
        headerPanel.add(buildLogoPanel());
        headerPanel.add(buildBufferPanel());
        headerPanel.add(buildHtmlActionsPanel());
    }

    private HorizontalLayoutContainer buildBufferPanel() {
        final HorizontalLayoutContainer buffer = new HorizontalLayoutContainer();
        buffer.setWidth("37%");
        return buffer;
    }

    private VerticalLayoutContainer buildLogoPanel() {
        VerticalLayoutContainer panel = new VerticalLayoutContainer();
        panel.setWidth("33%");

        Resources.ICONS.headerLogo();
        Image logo = new Image(Resources.ICONS.headerLogo().getSafeUri());
        logo.addStyleName(resources.css().iplantcLogo());
        logo.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                WindowUtil.open(Constants.CLIENT.iplantHome());
            }
        });

        panel.add(logo);

        return panel;
    }

    private HorizontalPanel buildHtmlActionsPanel() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setSpacing(10);
        panel.setWidth("20%");
        panel.add(buildActionsMenu(UserInfo.getInstance().getUsername(), 60, buildUserMenu()));
        panel.add(buildActionsMenu(I18N.DISPLAY.help(), 60, buildHelpMenu()));
        panel.add(buildNotificationMenu(I18N.DISPLAY.notifications(), 85));

        return panel;
    }

    private HorizontalLayoutContainer buildNotificationMenu(String menuHeaderText, int headerWidth) {
        final HorizontalLayoutContainer ret = new HorizontalLayoutContainer();
        lblNotifications = new NotificationIndicator(0);

        final PushButton button = new PushButton(menuHeaderText, headerWidth);
        notificationsView = new ViewNotificationMenu(eventBus);
        notificationsView.setBorders(false);
        notificationsView.setStyleName(resources.css().de_header_menu_body());
        notificationsView.setShadow(false);
        notificationsView.addShowHandler(new ShowHandler() {

            @Override
            public void onShow(ShowEvent event) {
                button.addStyleName(resources.css().de_header_menu_selected());

            }
        });

        notificationsView.addHideHandler(new HideHandler() {

            @Override
            public void onHide(HideEvent event) {
                button.removeStyleName(resources.css().de_header_menu_selected());
            }
        });
        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                // showNotificationWindow(Category.ALL);
                showHeaderActionsMenu(ret, notificationsView);
                lblNotifications.setCount(0);
            }
        });

        button.setImage(new Image(Resources.ICONS.menuAnchor()));
        ret.add(button);
        ret.add(lblNotifications);

        return ret;
    }

    private HorizontalLayoutContainer buildActionsMenu(String menuHeaderText, int headerWidth,
            final Menu menu) {
        final HorizontalLayoutContainer ret = new HorizontalLayoutContainer();
        ret.setBorders(false);

        final PushButton button = new PushButton(menuHeaderText, headerWidth);
        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                showHeaderActionsMenu(ret, menu);

            }
        });

        menu.addShowHandler(new ShowHandler() {

            @Override
            public void onShow(ShowEvent event) {
                button.addStyleName(resources.css().de_header_menu_selected());
            }
        });

        menu.addHideHandler(new HideHandler() {

            @Override
            public void onHide(HideEvent event) {
                button.removeStyleName(resources.css().de_header_menu_selected());
            }
        });

        button.setImage(new Image(Resources.ICONS.menuAnchor()));
        ret.add(button);

        return ret;
    }

    private Menu buildUserMenu() {
        final Menu userMenu = buildMenu();

        userMenu.add(new IPlantAnchor(I18N.DISPLAY.logout(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // doLogout();
                presenter.doLogout();
                userMenu.hide();
            }
        }));
        userMenu.add(new IPlantAnchor(I18N.DISPLAY.preferences(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                buildAndShowPreferencesDialog();
                userMenu.hide();
            }
        }));
        userMenu.add(new IPlantAnchor(I18N.DISPLAY.collaborators(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ManageCollaboratorsDailog dialog = new ManageCollaboratorsDailog();
                dialog.show();
                userMenu.hide();
            }
        }));

        return userMenu;
    }

    private void buildAndShowPreferencesDialog() {
        PreferencesDialog d = new PreferencesDialog();
        d.show();
    }

    private Menu buildHelpMenu() {
        final Menu helpMenu = buildMenu();
        helpMenu.add(new IPlantAnchor(I18N.DISPLAY.documentation(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WindowUtil.open(Constants.CLIENT.deHelpFile());
                helpMenu.hide();
            }
        }));
        helpMenu.add(new IPlantAnchor(I18N.DISPLAY.forums(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WindowUtil.open(Constants.CLIENT.forumsUrl());
                helpMenu.hide();
            }
        }));
        helpMenu.add(new IPlantAnchor(I18N.DISPLAY.contactSupport(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WindowUtil.open(Constants.CLIENT.supportUrl());
                helpMenu.hide();
            }
        }));
        helpMenu.add(new IPlantAnchor(I18N.DISPLAY.about(), -1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // displayAboutDe();
                EventBus.getInstance().fireEvent(new ShowAboutWindowEvent());
                helpMenu.hide();
            }
        }));

        return helpMenu;
    }

    private Menu buildMenu() {
        Menu d = new Menu();

        d.setSize("110px", "90px");
        d.setBorders(true);
        d.setStyleName(resources.css().de_header_menu_body());
        d.setShadow(false);

        return d;
    }

    private void showHeaderActionsMenu(HorizontalLayoutContainer anchor, Menu actionsMenu) {
        // show the menu so that its right edge is aligned with with the anchor's right edge,
        // and its top is aligned with the anchor's bottom.
        Point point = new Point(anchor.getAbsoluteLeft(), anchor.getAbsoluteTop());
        //
        actionsMenu.showAt(point.getX() + anchor.getElement().getWidth(true) + 2, point.getY()
                + anchor.getElement().getHeight(true) + 25);
    }

    @Override
    public void setPresenter(DEView.Presenter presenter) {
    	this.presenter = presenter;
    }

    /**
     * A Label with a setCount method that can set the label's styled text to the count when it's greater
     * than 0, or setting empty text and removing the style for a count of 0 or less.
     * 
     * @author psarando
     * 
     */
    private class NotificationIndicator extends HTML {

        int count;

        public NotificationIndicator(int initialCount) {
            super();

            setStyleName(resources.css().de_notification_indicator());
            setCount(initialCount);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
            if (count > 0) {
                setText(String.valueOf(count));
                addStyleName(resources.css().de_notification_indicator_highlight());
                Window.setTitle("(" + count + ") " + I18N.DISPLAY.rootApplicationTitle());
            } else {
                setHTML(SafeHtmlUtils.fromSafeConstant("&nbsp;&nbsp;"));
                removeStyleName(resources.css().de_notification_indicator_highlight());
                Window.setTitle(I18N.DISPLAY.rootApplicationTitle());
            }
        }
    }

    @Override
    public List<WindowState> getOrderedWindowStates() {
        return desktop.getOrderedWindowStates();
    }

    @Override
    public void restoreWindows(List<WindowState> windowStates) {
        for (WindowState ws : windowStates) {
            desktop.restoreWindow(ws);
        }
    }
}
