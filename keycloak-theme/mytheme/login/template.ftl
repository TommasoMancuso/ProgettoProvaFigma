<#macro mainLayout>
<!DOCTYPE html>
<html lang="it">

<head>
    <#include "partials/head.ftl">
</head>

<body>

    <#include "partials/navbar.ftl">

    <main>
        <#nested>
    </main>

    <#include "partials/footer.ftl">
    <#include "partials/bootstrap.ftl">

    <script src="${url.resourcesPath}/js/custom.js"></script>

</body>
</html>
</#macro>