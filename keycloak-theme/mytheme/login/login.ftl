<!DOCTYPE html>
<html lang="it">

<#include "partials/head.ftl">

<body>

<#include "partials/navbar.ftl">

<div class="container-fluid">
    <div class="row justify-content-center">

        <div class="col-12">
            <h1 class="text-center titles">ACCEDI</h1>
        </div>

        <div class="col-12 col-md-5">

            <#if message?has_content>
                <div class="alert alert-danger text-center">
                    ${message.summary}
                </div>
            </#if>

            <form id="kc-form-login"
                  action="${url.loginAction}"
                  method="post">

                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input class="form-control"
                           type="text"
                           name="username"
                           autocomplete="username"
                           required />
                </div>

                <div class="mb-3">
                    <label class="form-label">Password</label>
                    <input class="form-control"
                           type="password"
                           name="password"
                           autocomplete="current-password"
                           required />
                </div>

                <button class="btn btn-primary w-100" type="submit">
                    ACCEDI
                </button>

            </form>

            <div class="text-center mt-3">
                <a href="${url.registrationUrl}">
                    Non sei registrato? Registrati
                </a>
            </div>

        </div>

    </div>
</div>

<#include "partials/footer.ftl">
<#include "partials/bootstrap.ftl">

</body>
</html>