package org.iplantc.de.client.desktop.presenter;

import org.iplantc.core.resources.client.messages.I18N;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.views.gxt3.dialogs.IplantInfoBox;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.desktop.views.DEFeedbackView;
import org.iplantc.de.client.desktop.views.DEFeedbackView.Presenter;
import org.iplantc.de.client.desktop.views.DEFeedbackViewImpl;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class DEFeedbackPresenter implements Presenter {

    DEFeedbackView view;
    private Command callbackCommand;

    public DEFeedbackPresenter(Command callbackCommand) {
        this.callbackCommand = callbackCommand;
        view = new DEFeedbackViewImpl();
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void validateAndSubmit() {
        // System.out.println("-->" + view.validate());
        if (view.validate()) {
            // System.out.println("-->" + view.toJson());
            Services.DE_FEEDBACK_SERVICE.submitFeedback(view.toJson().toString(), new AsyncCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    if (callbackCommand != null) {
                        callbackCommand.execute();
                    }
                    IplantInfoBox info = new IplantInfoBox(I18N.DISPLAY.feedbackTitle(), I18N.DISPLAY
                            .feedbackSubmitted());
                    info.show();
                }

                @Override
                public void onFailure(Throwable caught) {
                    if (callbackCommand != null) {
                        callbackCommand.execute();
                    }
                    ErrorHandler.post(I18N.ERROR.feedbackServiceFailure(), caught);

                }
            });
        } else {
            AlertMessageBox amb = new AlertMessageBox(I18N.DISPLAY.warning(),
                    I18N.DISPLAY.publicSubmitTip());
            amb.setModal(true);
            amb.show();
        }

    }
}
