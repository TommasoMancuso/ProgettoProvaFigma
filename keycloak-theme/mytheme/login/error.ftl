<!DOCTYPE html>
<html lang="it">

<#include "partials/head.ftl">

<body>

<#include "partials/navbar.ftl">

<div class="container text-center py-5">

    <h1 class="text-danger">Si è verificato un errore</h1>

    <p class="mt-3">
        ${message?if_exists?html}
    </p>

    <a class="btn btn-primary mt-3" href="${url.loginUrl}">
        Torna al login
    </a>

</div>

<#include "partials/footer.ftl">
<#include "partials/bootstrap.ftl">

</body>
</html>