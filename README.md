hSmart_Android
===============

Description:
--------------------
    This porject is part of my bachelor graduation_design
    "Design of Embedded Remote Mmonitoring Platform"
    Hardware:
        Amazon kindle fire
    Software:
        Android2.2

Abstract:
--------------------
    request data(xml) from embedded linux server (with apache.http(http1.1 protocol))
    generate html page from xml_data
    represent the html page (with webview)
    eg:
        To control the remote switch
            generate dynamic html page
            click the page (call javascript function)
            call Android API (send Get_request)
        
### ctrls
    webview (support javascript)
    
### lib
    apache.http
    
