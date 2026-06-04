<#macro mainLayout>
<!DOCTYPE html>
<html lang="it">

<head>
    <#include "partials/head.ftl">
</head>

<body class="d-flex flex-column min-vh-100">

    <#include "partials/navbar.ftl">

    <main class="flex-grow-1">
        <#nested>
    </main>

    <#include "partials/footer.ftl">

    <#include "partials/bootstrap.ftl">

    <script src="${url.resourcesPath}/js/custom.js"></script>

</body>
</html>
</#macro>