<#import "template.ftl" as layout>

<@layout.mainLayout>

<div class="container-fluid">

    <div class="row justify-content-center">

        <div class="col-12">
            <h1 class="text-center titles">REGISTRATI</h1>
        </div>

        <div class="col-12 col-md-9">

            <form id="kc-register-form"
                  action="${url.registrationAction}"
                  method="post">

                <#if message?? && message.type == 'error'>
                    <div class="alert alert-danger">
                        ${message.summary}
                    </div>
                </#if>

                <#-- CAMPI STANDARD KEYCLOAK -->
                <div class="row g-3">

                    <div class="col-md-6">
                        <label>Email</label>
                        <input class="form-control"
                               name="email"
                               type="email"
                               required />
                    </div>

                    <div class="col-md-6">
                        <label>Password</label>
                        <input class="form-control"
                               name="password"
                               type="password"
                               required />
                    </div>

                    <#-- ATTRIBUTI CUSTOM -->
                    <#list profile.attributes as attribute>
                        <div class="col-md-6">

                            <label class="form-label">
                                ${attribute.displayName!attribute.name}
                            </label>

                            <input class="form-control"
                                   type="text"
                                   name="${attribute.name}"
                                   value="${(attribute.value!'')}" />

                        </div>
                    </#list>

                </div>

                <div class="text-center py-3">

                    <input class="btn-primary"
                           type="submit"
                           value="REGISTRATI">

                </div>

            </form>

        </div>

    </div>

</div>

</@layout.mainLayout>