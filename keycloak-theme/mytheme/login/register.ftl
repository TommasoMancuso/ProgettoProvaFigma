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

                    <!-- PASSWORD -->
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

                    <!-- ========================= -->
                    <!-- USER PROFILE ATTRIBUTES -->
                    <!-- ========================= -->

                    <!-- IMPORTANTISSIMO:
                         Qui NON inventiamo strutture FreeMarker.
                         Keycloak User Profile viene gestito backend-side.
                         Gli attributi custom funzionano SOLO via naming convention -->

                    <!-- FIRST NAME -->
                    <div class="col-md-6">
                        <label class="form-label">Nome</label>
                        <input class="form-control"
                               type="text"
                               name="firstName"
                               value="${(register.formData.firstName!'')}">
                    </div>

                    <!-- LAST NAME -->
                    <div class="col-md-6">
                        <label class="form-label">Cognome</label>
                        <input class="form-control"
                               type="text"
                               name="lastName"
                               value="${(register.formData.lastName!'')}">
                    </div>

                    <!-- PHONE (CUSTOM ATTRIBUTE) -->
                    <div class="col-md-6">
                        <label class="form-label">Telefono</label>
                        <input class="form-control"
                               type="text"
                               name="user.attributes.phone"
                               value="${(register.formData['phone']!'')}">
                    </div>

                    <!-- BIRTHDATE (CUSTOM ATTRIBUTE) -->
                    <div class="col-md-6">
                        <label class="form-label">Data di nascita</label>

                        <input class="form-control"
                            type="date"
                            name="user.attributes.birthDate"
                            id="birthDate"
                            required>
                    </div>

                </div>

                <div class="text-center py-4">
                    <input type="submit" class="btn-primary" value="REGISTRATI">
                </div>

            </form>

        </div>

    </div>

</div>

</@layout.mainLayout>