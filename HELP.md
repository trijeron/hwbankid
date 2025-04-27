# Getting Started

### Reference Documentation

Zadání:



Vytvořte Spring Boot aplikaci (Kotlin případně Java), která bude mít následující vlastnosti:

·         Bude fungovat jako samostatná služba bez runtime závislostí, pokud není specifikováno jinak.

·         Bude poskytovat funkcionalitu pro získání kurzového lísku na základě ČNB https://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.txt nebo jako XML

https://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xmla jednoho externího API se seznamu na githubu https://github.com/public-apis/public-apis?tab=readme-ov-file#currency-exchange

·         Implementuje následující 2 API

·         Seznam podporovaných párů měn

·         Pro měnový pár vrátí rozdíl v kurzu mezi ČNB a vybraným providerem.

·         Služba poskytuje healthcheck

·         Služba bude zabezpečená pomocí Basic Auth nebo Oauth (použijte případně Google jako OAuth server)

Technické řešení:
Dvě služby. Vrátí všechny měny z ČNB a z externího API. Získá rozdíl mezi kurzy a vrátí je jako JSON. Služba bude mít zabezpečený endpoint pomocí Basic Auth nebo Oauth (použijte případně Google jako OAuth server). Služba bude mít healthcheck endpoint.
Získávání dat bude asynchronní - normálně by se ukládalo do redisu, tady na to bude static structura. 
Healthcheck bude mít endpoint /actuator/health a bude obsahovat informaci o tom jestli aktualizace fungje
Naplnění dat bude probíhat jednou za 5 minut. Přes spring scheduler. Ve více podovém prostředí, 
by se mělo předat na řízení buď synchronizovaným quarzem, nebo přes scheduler platformy (třeba Kubernetes job), ale pak by na to vznikl endpoint

Co chybí:
V projektu není generální fault handler, většinou bývá specifikováno co se má vrátit. Řešil bych přes třídu @ControllerAdvice, která by měla metodu s @ExceptionHandler.Pro jednotlivé výjimky.
Pro 401 a 403 by bylo dobré mít třídu, která implementuje AuthenticationEntryPoint, AccessDeniedHandler
Asi bych vystavil API přes swagger


