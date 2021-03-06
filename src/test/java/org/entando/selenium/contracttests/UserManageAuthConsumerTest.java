package org.entando.selenium.contracttests;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.PactDslRequestWithPath;
import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import org.entando.selenium.pages.*;
import org.entando.selenium.utils.UsersTestBase;
import org.entando.selenium.utils.Utils;
import org.entando.selenium.utils.pageParts.Kebab;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static java.lang.Thread.sleep;
import static org.entando.selenium.contracttests.PactUtil.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserManageAuthProvider", port = "8080")
public class UserManageAuthConsumerTest extends UsersTestBase {

    @Autowired
    public DTDashboardPage dTDashboardPage;

    @Autowired
    public DTUsersPage dTUsersPage;

    @Autowired
    public DTUserManageAuthorityPage dTUserManageAuthorityPage;

    @BeforeAll
    public void setupSessionAndNavigateToUserManagement (){
        PactDslWithProvider builder = ConsumerPactBuilder.consumer("LoginConsumer").hasPactWith("LoginProvider");
        PactDslResponse accessTokenResponse = buildGetAccessToken(builder);
        PactDslResponse getUsersResponse = PactUtil.buildGetUsers(accessTokenResponse,1,1);
        getUsersResponse = PactUtil.buildGetUsers(getUsersResponse,1,10);
        PactDslResponse getPagesResponse = buildGetPages(getUsersResponse);
        PactDslResponse getPageStatusResponse = buildGetPageStatus(getPagesResponse);
        PactDslResponse getWidgetsResponse = buildGetWidgets(getPageStatusResponse);
        PactDslResponse getGroupsResponse = PactUtil.buildGetGroups(getWidgetsResponse);
        PactDslResponse getPageModelsResponse = buildGetPageModels(getGroupsResponse);
        PactDslResponse getLanguagesResponse = buildGetLanguages(getPageModelsResponse);
        PactDslResponse getProfileTypesResponse = PactUtil.buildGetProfileTypes(getLanguagesResponse);
        MockProviderConfig config = MockProviderConfig.httpConfig("localhost", 8080);
        PactVerificationResult result = runConsumerTest(getProfileTypesResponse.toPact(), config, mockServer -> {
            login();

            dTDashboardPage.SelectSecondOrderLinkWithSleep("User Management", "Users");
            Utils.waitUntilIsVisible(driver, dTUsersPage.getAddButton());
        });
    }

    @Pact(provider = "UserManageAuthProvider", consumer = "UserManageAuthConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {

        PactDslResponse getAuthResponse = buildGetAuth(builder, 1,10);
        PactDslResponse getRolesResponse = PactUtil.buildGetRoles(getAuthResponse, 1,0);
        PactDslResponse getGroupsResponse = buildGetGroups(getRolesResponse);
        PactDslResponse postAuthResponse = buildPostAuth(getGroupsResponse);
        PactDslResponse getProfileTypesResponse = buildGetProfileTypes(postAuthResponse);
        PactDslResponse getUsersResponse = buildGetUsers(getProfileTypesResponse,1,10);
        return getUsersResponse.toPact();
    }

    private PactDslResponse buildGetAuth(PactDslWithProvider builder, int page, int pageSize) {
            PactDslRequestWithPath optionsRequest = builder
                    .uponReceiving("The authorizations OPTIONS Interaction")
                    .path("/entando/api/users/UNIMPORTANT/authorities")
                    .method("OPTIONS");
            PactDslResponse optionsResponse = optionsResponse(optionsRequest);
            PactDslRequestWithPath request = optionsResponse.uponReceiving("The authorizations GET Interaction")
                    .path("/entando/api/users/UNIMPORTANT/authorities")
                    .method("GET");
            return standardResponse(request, "{\"payload\":[],\"errors\":[],\"metaData\":{}}");
        }

    private PactDslResponse buildGetUsers(PactDslResponse builder, int page, int pageSize) {
        PactDslRequestWithPath request = builder.uponReceiving("The User Query GET Interaction")
                .path("/entando/api/users")
                .method("GET")
                .matchQuery("page", "\\d+", "" + page)
                .matchQuery("pageSize",  "\\d+", ""+pageSize);
        return standardResponse(request, "{\"payload\":[{\"username\":\"UNIMPORTANT\",\"registration\":\"2018-08-31 00:00:00\",\"lastLogin\":null,\"lastPasswordChange\":null,\"status\":\"active\",\"accountNotExpired\":true,\"credentialsNotExpired\":true,\"profileType\":null,\"profileAttributes\":{},\"maxMonthsSinceLastAccess\":-1,\"maxMonthsSinceLastPasswordChange\":-1}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":1,\"totalItems\":1,\"sort\":\"username\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    private PactDslResponse buildGetGroups(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The Groups OPTIONS Interaction")
                .path("/entando/api/groups")
                .method("OPTIONS")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "0");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The Groups GET Interaction")
                .path("/entando/api/groups")
                .method("GET")
                .matchQuery("page", "1")
                .matchQuery("pageSize", "0");
        String json = "{\"payload\":[{\"code\":\"testgroup4pact\",\"name\":\"testgroup4pact\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":1,\"lastPage\":7,\"totalItems\":7,\"sort\":\"code\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}";
        return standardResponse(request, json);
    }

    private PactDslResponse buildPostAuth(PactDslResponse builder) {
        PactDslRequestWithPath optionsRequest = builder.uponReceiving("The authorization post OPTIONS Interaction")
                .path("/entando/api/users/UNIMPORTANT/authorities")
                .method("OPTIONS");
        PactDslResponse optionsResponse = optionsResponse(optionsRequest);
        PactDslRequestWithPath request = optionsResponse.uponReceiving("The authorization POST Interaction")
                .path("/entando/api/users/UNIMPORTANT/authorities")
                .method("POST");
        return standardResponse(request, "{\"payload\":[{\"group\":\"testgroup4pact\",\"role\":\"UNIMPORTANT\"}],\"errors\":[],\"metaData\":{}}");
    }

    private PactDslResponse buildGetProfileTypes(PactDslResponse builder) {

        PactDslRequestWithPath request = builder
                .uponReceiving("The ProfileTypes GET Interaction")
                .path("/entando/api/profileTypes")
                .method("GET")
                .matchQuery("page", "\\d+")
                .matchQuery("pageSize", "\\d+");
        return standardResponse(request, "{\"payload\":[{\"code\":\"PFL\",\"name\":\"Default user profile\",\"status\":\"0\"},{\"code\":\"1DF\",\"name\":\"1SeleniumTest_DontTouch\",\"status\":\"0\"}],\"errors\":[],\"metaData\":{\"page\":1,\"pageSize\":10,\"lastPage\":1,\"totalItems\":1,\"sort\":\"name\",\"direction\":\"ASC\",\"filters\":[],\"additionalParams\":{}}}");
    }

    @Test
    public void runTest() throws InterruptedException {

        Kebab kebab = dTUsersPage.getTable().getKebabOnTable("UNIMPORTANT", usersTableHeaderTitles.get(0), usersTableHeaderTitles.get(4));
        kebab.getClickable().click();
        Utils.waitUntilIsVisible(driver, kebab.getAllActionsMenu());
        kebab.getAction("Manage authorization for: UNIMPORTANT").click();
        dTUserManageAuthorityPage.getUserGroup().selectByVisibleText("testgroup4pact");
        dTUserManageAuthorityPage.getUserRole().selectByVisibleText("UNIMPORTANT");
        dTUserManageAuthorityPage.getAddButton().click();
        dTUserManageAuthorityPage.getSaveButton().click();
    }
}


