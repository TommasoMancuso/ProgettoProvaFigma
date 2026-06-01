<#macro mainLayout>
<!DOCTYPE html>
<html>
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

</body>
</html>
</#macro>