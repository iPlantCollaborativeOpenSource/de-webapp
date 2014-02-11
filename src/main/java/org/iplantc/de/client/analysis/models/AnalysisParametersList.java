/**
 * 
 */
package org.iplantc.de.client.analysis.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author sriram
 * 
 */
public interface AnalysisParametersList {

    @PropertyName("parameters")
    List<AnalysisParameter> getParameterList();
}
