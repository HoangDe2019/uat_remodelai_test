package internal

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.main.TestCaseMain


/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */
public class GlobalVariable {
     
    /**
     * <p>Profile reminders_profile : domain &#47; ip address </p>
     */
    public static Object api_base_url
     
    /**
     * <p></p>
     */
    public static Object email
     
    /**
     * <p></p>
     */
    public static Object password
     
    /**
     * <p></p>
     */
    public static Object last_name
     
    /**
     * <p></p>
     */
    public static Object first_name
     
    /**
     * <p></p>
     */
    public static Object phone_number
     
    /**
     * <p></p>
     */
    public static Object auToken
     
    /**
     * <p></p>
     */
    public static Object email2
     
    /**
     * <p></p>
     */
    public static Object newCompanyId
     
    /**
     * <p></p>
     */
    public static Object ui_base_url
     
    /**
     * <p></p>
     */
    public static Object admin_email_01
     
    /**
     * <p></p>
     */
    public static Object admin_password_01
     
    /**
     * <p></p>
     */
    public static Object currentTestSuiteId
     
    /**
     * <p></p>
     */
    public static Object currentTestCaseId
     
    /**
     * <p></p>
     */
    public static Object currentTestCaseName
     

    static {
        try {
            def selectedVariables = TestCaseMain.getGlobalVariables("default")
			selectedVariables += TestCaseMain.getGlobalVariables(RunConfiguration.getExecutionProfile())
            selectedVariables += TestCaseMain.getParsedValues(RunConfiguration.getOverridingParameters(), selectedVariables)
    
            api_base_url = selectedVariables['api_base_url']
            email = selectedVariables['email']
            password = selectedVariables['password']
            last_name = selectedVariables['last_name']
            first_name = selectedVariables['first_name']
            phone_number = selectedVariables['phone_number']
            auToken = selectedVariables['auToken']
            email2 = selectedVariables['email2']
            newCompanyId = selectedVariables['newCompanyId']
            ui_base_url = selectedVariables['ui_base_url']
            admin_email_01 = selectedVariables['admin_email_01']
            admin_password_01 = selectedVariables['admin_password_01']
            currentTestSuiteId = selectedVariables['currentTestSuiteId']
            currentTestCaseId = selectedVariables['currentTestCaseId']
            currentTestCaseName = selectedVariables['currentTestCaseName']
            
        } catch (Exception e) {
            TestCaseMain.logGlobalVariableError(e)
        }
    }
}
