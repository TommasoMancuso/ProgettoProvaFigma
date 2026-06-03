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

                <div class="row g-3">

                    <!-- USERNAME -->
                    <div class="col-md-6">
                        <label class="form-label">Username</label>
                        <input class="form-control"
                               type="text"
                               name="username"
                               value="${(register.formData.username!'')}"
                               required>
                    </div>

                    <!-- EMAIL -->
                    <div class="col-md-6">
                        <label class="form-label">Email</label>
                        <input class="form-control"
                               type="email"
                               name="email"
                               value="${(register.formData.email!'')}"
                               required>
                    </div>

                    <!-- PASSWORD (KEYCLOAK MANAGED - NO CUSTOM UI) -->
                    <div class="col-md-6">
                        <label class="form-label">Password</label>
                        <input class="form-control"
                               type="password"
                               name="password"
                               required>
                    </div>

                    <!-- CONFIRM PASSWORD -->
                    <div class="col-md-6">
                        <label class="form-label">Conferma Password</label>
                        <input class="form-control"
                               type="password"
                               name="password-confirm"
                               required>
                    </div>

                </div>

                <!-- SUBMIT -->
                <div class="text-center py-4">

                    <input type="submit" class="btn-primary" value="REGISTRATI">

                </div>

            </form>

        </div>

    </div>

</div>

</@layout.mainLayout>