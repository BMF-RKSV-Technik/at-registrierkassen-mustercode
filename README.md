# 10.02.2016: ACHTUNG: Brutto vs. Netto
Das BMF hat heute folgende Information bekanntgegeben. Der Mustercode bzw. die Hinweise auf diese Thematik auf dieser Seite werden in den nächsten Version aktualisiert. Diese Information wird auch in den FAQs der WKO bekannt gegeben:

Aufgrund mehrfacher Anfragen und unterschiedlicher Aussagen in den FAQ-WKO und FAQ-Muster-Code erfolgen folgende Klarstellungen durch das BMF:
 
- Bei der Aufteilung der Barzahlung gemäß §§ 9 Abs. 2 Z 4, 10 Abs. 2 Z 4 und 11 Abs. 1 Z 3 RKSV sind beim jeweiligen Steuersatz immer Bruttobeträge (Betrag inkl. USt.) anzugeben.
- Dem Satz-Null sind Bruttobeträge zuzuordnen, die entweder von der USt. befreit sind, beim Unternehmer nicht, nicht zur Gänze bzw. mit einem anderen % als den % des Signaturformates (derzeit 20% für Satz-Normal, 10% für Satz-Ermaessigt-1, 13% für Satz-Ermaessigt-2 und 19% für Satz-Besonders) der USt. unterliegen oder deren USt. auf Grund anderer Unterlagen geschuldet wird. Darunter fallen beispielsweise Beträge, die im Namen und auf Rechnung dritter vereinnahmt werden (durchlaufende Posten), nicht zur Gänze besteuert sind (Differenzzahlungen) oder auf eine Rechnung verweisen.
- Sämtliche Beträge in Belegen, die Barzahlungen darstellen oder mit zumindest einer Barzahlung kombiniert sind, sind als Bruttobeträge (inkl. USt.) zu signieren und in den Umsatzzähler aufzunehmen. Dies gilt auch für Stornobuchungen, nicht jedoch für Trainingsbuchungen. Eine freiwillige Signierung von Beträgen aus Belegen ohne Signierungspflicht (z.B.: Belege ohne Barzahlungen) mit Aufnahme in den Umsatzzähler ist immer möglich. Der Umsatzzähler ist als Summenzähler Teil der Sicherheitseinrichtung einer Registrierkasse und für Umsatzanalysen nur in Verbindung mit den weiteren, in der Registrierkasse aufgezeichneten Daten vorgesehen.


# Wichtige Informationen
 - **Testfälle**: Es wird für die Hersteller neben dem Prüfwerkzeug auch eine Sammlung von Test-Fällen geben, die die möglichen Kombinationen aus unterschiedlichen Belegen und das korrekte Verhalten abdecken (z.B. Verhalten bei Trainingsbuchung, Verhalten bei ausgefallener Signatureinrichtung, Verhalten bei Startbeleg, Verhalten bei Wechsel von Signaturzertifikat). Die Hersteller sollen sich an diesen Testfällen orientieren, Belege anhand der Testfälle erstellen und dann über das Prüfwerkzeug überprüfen.
 - **Korrekte Implementierung in diesem DEMO-Projekt**: Es wurde darauf geachtet die Qualität des Demo-Codes sehr hoch zu halten, dennoch können Fehler nicht ausgeschlossen werden. Um hier eine sehr hohe Qualität erreichen zu können, werden für die nächsten Versionen die im vorigen Punkt angesprochenen Testfälle erstellt und auch im Muster-Code umgesetzt.

# Change Log
 - **25.11.2015**: Release 0.5 veröffentlicht
	 - **Information**:
		 - **Brutto/Netto Beträge im Steuersatz:** Es werden Netto-Beträge in den Feldern für die Steuer-Sätze verwendet. Somit werden auch Netto-Beträge zum Umsatzzähler addiert/subtrahiert.
	 - **Änderungen**:
		 - **Trainingsbuchungen**: tatt "TRAIN" wird die Zeichenkette "TRA" Base64-kodiert im Umsatzzähler abgelegt. Damit ist es nun aufgrund der Länge eindeutig zwischen einem echtem verschlüsselten Umsatzzähler und Storno/Trainings-Belegen zu unterscheiden (Der Umsatzzähler muss min. 5 byte lang sein, daher ist diese eindeutige Unterscheidung mit diesen kurzen Zeichenketten möglich).
		 - **Steuer Satz "Null"** muss in Umsatzzähler eingehen, es gibt also keinen Unterschied zu den anderen Steuersätzen (In Release 0.4 hatte dieser Satz keinen Einfluss auf den Umsatzzähler).
	 - **Features**:
		 - **Storno**:  Storno-Belege werden im Feld Umsatzzähler markiert. Statt dem Umsatzzähler wird hier der BASE64 kodierte Wert der Zeichenkette "STO" verwendet. Damit ist eine eindeutige Erkennung von Stornobelegen möglich. Ein Stornobeleg darf nicht mit normalen Buchungen vermischt werden, es können aber mehere Steuersätze gleichzeitig in einem Beleg storniert werden. Storno-Beträge können positive/negative Werte enthalten, wobei ein negativer Wert wohl der gängige Fall sein wird. Der Umsatzzähler wird bei einem Stornobeleg natürlich beeinflusst (im Gegensatz zu einer Trainingsbuchung). 
		 - **Verschlüsselung**: Da der CTR Modus nicht in allen Programmiersprachen verfügbar ist, wurden zwei weitere Varianten hingefügt, die zeigen wie die Ver/Entschlüsselung mit AES-CFB oder AES-ECB durchgeführt werden kann. Siehe Methode *updateTurnOverCounterAndAddToDataToBeSigned* in Klasse [DemoCashBox](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/DemoCashBox.java) 
 - **29.10.2015**: Release 0.4 veröffentlicht (Bitte **unbedingt die Hinweise vom 30.10.2015** beachten, wurden einen Tag nach Veröffentlichung der Release hinzugefügt).
	 - Features:
		 - Demo:
			 - Trainingsbuchungen sind nun inkludiert (siehe [FAQ](https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erl%C3%A4uterungen-FAQ)):
				 - **WICHTIGER HINWEIS 30.10.2015**: Aktuelle Vorgehensweise muss noch geklärt werden, da die Verordnung die Kennzeichung des maschinelesbaren Codes mit "Trainingsbuchung" fordert, dies aber bei der aktuellen Lösung genau genommen nicht der Fall ist. ("TRAIN" statt Umsatzzähler).
			 - Stornobuchungen sind nun inkludiert: erkennbar durch negative Werte (siehe [FAQ](https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erl%C3%A4uterungen-FAQ)): 
				 - **WICHTIGER HINWEIS 30.10.2015**: in der aktuellen Version werden Stornos und Buchungen vermischt. Diese Vermischung darf nicht vorkommen. Das aktuelle Beispiel zeigt nur wie der Umsatzzähler davon betroffen ist. Korrekte Stornobuchungen werden in der nächsten Version hinzugefügt bzw. auch on den Prüfwerkzeugen unterstützt.
				 - **WICHTIGER HINWEIS 30.10.2015**: Eine Stornobuchung kann durch ein negative Vorzeichen eindeutig erkannt werden. Allerdings ist bei der in 0.4 demonstrierten Lösung nicht die in der Verordnung geforderte Kennzeichnung "Stornobuchung" enthalten.
			 - "Manuelles JWS" Modul hinzugefügt: Dieses Modul zeigt wie die JWS Signatur sehr einfach ohne externe Libs erstellt werden kann (siehe [ManualJWSModule](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/jws/ManualJWSModule.java))
			 - Basis PKCS11-Signatureinheit hinzugefügt. PKCS11 ist ein Standard der weite Verwendung in Signaturprodukten findet. Um PKCS11 verwenden zu können müssen die Parameter im Modul angepasst werden (Pfad, Key Alias) (siehe [PKCS11SignatureModule](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/rawsignatureprovider/PKCS11SignatureModule.java))
		 - Prüfung:
			 - Prüfungsmodul kann nun Trainingsbuchungen überprüfen
		 - Änderungen nach Absprache mit BMF:
			 - Umsatzzähler repräsentiert nun €-Cent (vorher €). Damit entfällt die Problematik der Rundung (siehe [FAQ](https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erl%C3%A4uterungen-FAQ))
		 - Kleine Änderungen
			 - Demo Code generiert jetzt 50 statt 15 Belege
		 - Behobene Bugs (Demo und Prüfung)
			 - Verkettung: Es wurde fäschlicherweise im Demo-Code der rohe QR-Code des letzten Belegs für die Verkettung verwendet. In Übereinstimmung mit der Detailspezifikation muss dies die JWS-Kompakt-Repräsentation sein.
			 - MWST-Satz-Null: Dieser Betrag wurde fäschlicherweise zum Umsatzzähler addiert
				 - **WICHTIGER HINWEIS 30.10.2015**: Das Verhalten, dass der Null-Satz nicht zum Umsatzzähler addiert wird ist vor allem bei UST-befreiten Unternehmen problematisch.
			 - OCR-Rep: Die OCR-Bsps enthielten den gleichen Wert für Umsatzzähler und Verkettungswert
 - **14.10.2015**: Release 0.3 veröffentlicht
	 - Features:
		 - Demo:
			 - Simulation von beschädigter Signatureinrichtung in Demo, Standard Ergebnis für DEP-Export-Format enthält nun Belege die nicht signiert werden konnten
			 - Simulation von Zertifikatwechsel, Standard Ergebnis für DEP-Export-Format enthält nun immer zwei Zertifikate
			 - AES Schlüssel wird bei Demo mitausgegeben, damit ist Prüfung von Umsatzzähler möglich
		 - Prüfung:
			 - Prüfung von DEP-Export-Dateien die Belege von ausgefallenen Signatureinrichtungen beinhalten
			 - Prüfung akzeptiert nun "0,00" und "0.00" als Dezimalformat (Spezifikation sieht nur "0,00" vor)
			 - Prüfung überprüft nun ob in DEP-Export Kassen-ID/Belegnummer Kombination eindeutig (darf nur einmal vorkommen)
			 - Prüfung von korrekter Abbildung des Umsatzzählers (AES Schlüssel benötigt)
			 - Prüfung von DEP-Export-Files die mehr als ein Signaturzertifikat enthalten
	 - Behobene Bugs:
		 - Falsche Suite: https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/6
		 - Falsches Dezimalformat, Erstellung nun im Format 0,00. Prüfung akzeptiert 0.00 und 0,00 https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/2
		 - Falscher Identifier im DEP-Export-Format: https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/1
		 - Falsches Runden der MWST-Sätze beim Addieren für den Umsatzzähler https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/7
 - **12.10.2015**: Release 0.2 veröffentlicht
	* **Wichtige Änderungen**:
		* Erweiterung der Prüfwerkzeuge
			* Detaillierte Prüfung von einzelnen Belegen (vor allem Format)
			* Detaillierte Prüfung des DEP-Export-Formats (vor allem Format)
			* Detaillierte Rückmeldungen zu Formatfehlern (MWST-Sätze, BASE64, RK Suite etc.)
	* Detailänderungen
		* Hinzufügen von Provider-unabhängigen [Nimbus JWS Library](https://bitbucket.org/connect2id/nimbus-jose-jwt/wiki/Home) (Demo-Code noch nicht Provider-unabhängig)
		* Entfernen des rudimentären Prüf-Codes
- **02.10.2015**: Release 0.1 auf GitHub veröffentlicht


**Planung für weitere Releases**:
 - Qualitätssicherung, Integration von rudimentärer Kartenunterstützung
 - FAQ-Erstellung und Erweiterung, Qualitätskontrolle, hinzufügen von Hinweisen/Informationen, Erweiterungen

# Überblick
Dieses Projekt stellt Demo-Code als Begleitung zur Registrierkassensicherheitsverordnung (RKSV) (https://www.bmf.gv.at/steuern/Registrierkassensicherheitsverordnung.html) zur Verfügung. Der Demo-Code zeigt

* wie die wesentlichen Elemente der Detailspezifikation der Verordnung in Software implementiert werden können,
* gibt zusätzliche Erläuterungen zu Aspekten der Detailspezifikation die noch Interpretationsspielraum zulassen,
* und stellt Prüfwerkzeuge zur Verfügung, die es erleichtern die korrekte Umsetzung einer Registrierkasse im Sinne der Detailspezifikation zu überprüfen (diese Werkzeuge werden ständig erweitert, siehe ChangeLog).

In diesem Projekt werden vorwiegend technische Aspekte der Verordnung betrachtet. Die Informationen und der Code werden laufend erweitert und mit typischen Fragen/Antworten ergänzt.

## Wichtige Anmerkungen/Einschränkungen
* *Wichtige Anmerkung*: Die Versionen 0.1 bis 0.5 demonstrieren wie mit den unterschiedlichen Elementen (Signature, QR-Code etc.) umgegangen werden muss. Obwohl hier nach bestem Gewissen vorgegangen wurde, kann keine GARANTIE für die korrekte Funktionsweise übernommen werden. Um hier in den nächsten Versionen mehr Klarheit bieten zu können, werden vom aktuellen Code unabhängige Prüfwerkzeuge geschaffen. Diese ermöglichen den Kassaherstellern die jeweiligen Produkte (und auch diesen Demo-Code) so weit wie möglich auf das korrekte Verhalten mit Relevanz für die Verordnung zu überprüfen.
* *Sprache*: Diese Projektseite verwendet Deutsch als Sprache. In den textuellen Ergänzungen im Source Code wird Englisch verwendet.

##Weiteres Vorgehen
Diese Plattform wird für die weitere Bereitstellung von Demo-Code, für das Bereitstellen von Prüfwerkzeugen und für die Erstellung von FAQs verwendet. Die weiteren Versionen des Codes werden demnächst veröffentlicht und fokussieren sich auf erweiterte Verwendungsmuster und das Bereitstellen bzw. die Erweiterung von Prüfwerkzeugen die es ermöglichen erstellte Belege, DEP-Export-Dateien auf Ihre Korrektheit zu prüfen. Auch wird dieses Projekt laufend um Antworten zu häufig gestellten Fragen ergänzt.

##Kontakt/Fragen
Es wurde dazu eine Projektseite von der WKO eingerichtet. Es ist dazu eine Registrierung bei der WKO notwendig.

https://communities.wko.at/Kassensoftware/default.aspx

Etwaige Fragen sollten dort im Forum gestellt werden, um eine möglichst effizient die Beantwortung durchführen zu können. 

https://communities.wko.at/Kassensoftware/Lists/Forum/

Ausgewählte Fragen/Antworten werden aus diesem Forum übernommen und auf der hier verfügbaren WIKI Seite eingetragen.

https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erläuterungen-FAQ

## Lizenz
Der gesamte Code wird unter der Apache 2.0 Lizenz zur Verfügung gestellt.(http://www.apache.org/licenses/LICENSE-2.0). 

Alle verwendeten Dritt-Bibliotheken und deren Lizenzen sind in den Maven Build Dateien (pom.xml) der einzelnen Module ersichtlich und auf der folgenden WIKI Seite zusammengefasst:

https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Lizenzen-Dritt-Bibiliotheken

# Verwendung des Democodes und der Demokassa

##Ausführen des Codes (Verwenden der Downloadpakete)
Neben dem Source Code wird auch immer eine ZIP Datei der ausführbaren Dateien zur Verfügung gestellt. Die neueste Version ist immer unter [Releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases) zu finden.

###Voraussetzungen
* *Java VM*: Es wird eine aktuelle Java VM (JRE ausreichend) mit Version >= 1.7 benötigt.
* *Kryptographie*: Der Registrierkassen-Demo-Code verwendet starke Kryptographie (z.B. AES mit 256 bit Schlüssel), der mit den Standard-Export Policies der Java VM nicht ausgeführt werden kann. Es muss daher die "Unlimited Strength Policy" von Oracle installiert werden. Siehe: "http://www.oracle.com/technetwork/java/javase/downloads/index.html"

###Verwendung des Demo-Codes - Demokassa
Der Demo Code enthält aktuell eine einfache Testklasse die eine Demokassa ansteuert. Diese Demokassa bietet die Möglichkeit eine angegeben Anzahl von Belegen zu erstellen, diese in das DEP Export Format zu exportieren, und einfache Test-Belege als PDF zu erstellen, die die Daten als QR-Code oder OCR-Code beinhalten.

Download und entpacken von regkassen-demo-release-0.5.zip (siehe https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases).

Ausführen der Demokasse mit

      java -jar regkassen-demo-0.5.jar -o OUTPUT_DIR -n 20 -g -s
      
Wobei 

 - **"OUTPUT_DIR"** ein Verzeichnis ist, in dem die vom Demo-Code erstellten Daten/Belege geschrieben werden. Es wird in der aktuellen Version dazu ein zufällig genierter Signaturschlüssel verwendet. Wenn die Option **-o** nicht angegeben wird, dann wird im aktuellen Verzeichnis eines mit dem Prefix CashBox ersteerlällt.
 - Die Option **-n** gibt die Anzahl der zu erstellenden Belege an. Wenn sie nicht angegeben wird, werden 50 Belege erstellt.
 - Die Option **-s** gibt an, dass nur mit einem Signaturzertifikat gearbeitet werden soll. Wird die Option weggelassen wird mit zwei Signaturzertifikaten gearbeitet, um das Handling dieses Falles im Export-Format abbilden zu können.
 - Die Option **-g** gibt an, dass die Signatureinrichtung nicht ausfallen kann. Wird die Option weggelassen fällt die Signatureinrichtung zufällig aus und die Vorgehensweise bzw. die Recovery-Prozedur wird gezeigt.
 
Das Output-Verzeichnis enthält folgende Dateien/Verzeichnisse:

 - **Datei dep-export.txt**: Die generierten Belege im DEP Export Format (Detailspezifikation, Abs 3)
 - **Datei qr-code-rep.txt**: Die textuelle Representation der maschinenlesbaren QR-Codes (der Inhalt der QR-Codes). Eine Zeile der Datei entspricht der QR-Code Repräsentation eines Belegs.
 - **Datei ocr-code-rep.txt**: Die textuelle Representation der maschinenlesbaren OCR-Codes. Eine Zeile der Datei entspricht der OCR-Code Repräsentation eines Belegs.
 - **Datei signatureCertificates.txt**: Die/das verwendete(n) Signatur-Zertifikat(e) im BASE64-kodierten DER-Format
 - **Datei signatureCertificateChains.txt**: Die zu dem/den Signatur-Zertifikat(en) passende(n) Zertifikatsketten 
 - **Datei aesKeyBase64.txt:** Der BASE64-kodierte AES-Schlüssel für die Ver/Entschlüsselung des Umsatzzählers im Beleg
 - **Verzeichnis ocr-code-dir**: PDF-Belege die mit dem OCR-Code bedruckt wurden
 - **Verzeichnis qr-code-dir**: PDF-Belege die mit dem QR-Code bedruckt wurden

Ein Beispiel für den Output ist auch direkt verfügbar: example-output-0.5.zip (siehe https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases).
Code dazu: siehe Klasse [SimpleDemo](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-democashbox/src/main/java/at/asitplus/regkassen/demo/SimpleDemo.java).

###Verwendung des Prüfwerkzeugs
In Version 0.5 wurden die Prüfwerkzeuge um weitere Prüfungen erweitert (Ausgefallene Signatureinrichtung, Veschlüsselter Umsatzzähler, etc.). Neben dem DEP Export Format können nun auch einzelne QR-Code-Bsp von Belegen geprüft werden.
Download und entpacken von regkassen-demo-release-0.5.zip (siehe https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases).

**DEP-Export Format**

    java -jar regkassen-verification-depformat-0.5.jar -i DEP-EXPORT-FILE -k AES-KEY-FILE
	         
Wobei

 - **DEP-EXPORT-FILE** der im vorigen Beispiel erstellten *dep-export.txt* Datei entspricht. Für den schnellen Test kann die Datei *dep-export.txt* aus dem Beipspiel übernommen werden.
 - **AES-KEY-FILE** der im vorigen Beispiel erstellen "*aesKeyBase64.txt*" Datei entspricht. Für den schnellen Test kann die Datei *aesKeyBase64.txt* aus dem Beipspiel übernommen werden.

**QR-Code-Repräsentation eines einzelnen Belegs oder mehrerer Belege**

    java -jar regkassen-verification-receipts-0.5.jar -i QR-CODE-REP-FILE -s SIGNATURE-CERTIFICATES-FILE

Wobei

 - **QR-CODE-REP-FILE** der im vorigen Beispiel erstellen *qr-code-rep.txt* Datei Datei entspricht. Für den schnellen Test kann die Datei *qr-code-rep.txt* aus dem Beispiel übernommen werden.
 - **SIGNATURE-CERTIFICATE-FILE** der im vorigen Beispiel erstellen *signatureCertificates.txt* Datei entspricht. Für den schnellen Test kann die Datei *signatureCertificates.txt* aus dem Beispiel übernommen werden.

**Anmerkung**: Sollten sich mehrere Belege in der Datei **QR-CODE-REP-FILE** befinden, so wird deren Verkettung **NICHT** überprüft. Diese Prüfung wird nur bei der DEP-Export-Format Prüfung durchgeführt.
                    
 
##BUILD Prozess, Details zum Code
Das Projekt ist in drei Maven Module aufgeteilt:
 - **regkassen-core**: Dieses Modul enthält den Code der für die Erstellung und Signatur der Belege notwendig ist.
 - **regkassen-democashbox**: Dieses Modul verwendet das regkassen-core Modul und setzt Demo-Use Cases um.

###Übersicht über den Code
 - [at.sitplus.regkassen.core.base](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/base): Diese Package enthält Basisdatenstrukturen, Hilfsfunktionen und die in der Detailspezifikation definierten RegKassen Suite. (Detailspezifikation, Abs 2). Link zu Code
 - [at.asitplus.regkassen.core.modules](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules):  In diesem Package sind die Module der Kassa enthalten.
	 - [DEP](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/DEP): Modul für die Implementierung des Datenerfassungsprotokolls. Wesentliche Funktionalität hier ist der EXPORT des DEPs (implementiert in *[SimpleMemoryDEPModule](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/DEP/SimpleMemoryDEPModule.java)*)
	 - [init](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/init): Parameter für die Initialisierung der Registrierkasse
	 - [print](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/print): Einfache Version eines PDF Druckers, der QR-Codes und OCR-Codes auf Belege druckt.
	 - [signature](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature): Dieses Package besteht aus zwei Hauptkomponenten:
		 - [jws](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/jws): Mit dem Modul [OrgBitbucketBcJwsModule](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/jws/OrgBitbucketBcJwsModule.java) oder [ComNimbusdsJwsModule](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/jws/ComNimbusdsJwsModule.java) werden die JWS Signaturen erstellt. Neu hinzugekommen ist das Modul [ManualJWSModule](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/jws/ManualJWSModule.java), das keine externe Bibliothek für das Erstellen der JWS-Signatur benötigt. Die Module greifen auf das folgende Package zu:
		 - [rawsignatureprovider](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/rawsignatureprovider): Dieses Modul erstellt die wirkliche Signatur und kann eine Smartcard, ein HSM, ein Cloud-Dienst oder ein anderes Modul (im geschlossenen System) darstellen. In der aktuellen DEMO-Kassa sind zwei Module enthalten:
			 - [DemoSoftwareSignatureModule](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/rawsignatureprovider/DO_NOT_USE_IN_REAL_CASHBOX_DemoSoftwareSignatureModule.java): Hierbei handelt es sich um ein simples Software-basiertes Modul. Dieses DARF AUF KEINEN FALL in einer echten Kasse verwendet werden. In weiteren Demo-Code Versionen wird hier die Ansteuerung der Karte gezeigt.
			 - [PKCS11SignatureModule](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/modules/signature/rawsignatureprovider/PKCS11SignatureModule.java): Hierbei handelt es sich um ein Modul das den weit verbreiteten PKCS11 Standard für das Ansprechen von Krypto-Hardware unterstützt. Im Modul müssen noch die Parameter für die verwendete PKCS11-Bibliothek und die jeweiligen Schlüsselnamen angegeben werden.
 - [at.sitplus.regkassen.core](https://github.com/a-sit-plus/at-registrierkassen-mustercode/tree/master/regkassen-core/src/main/java/at/asitplus/regkassen/core): In diesem Package befindet sich Demo-Registrierkasse (DemoCashBox). Diese Klasse verwendet die oben genannten Module um Belege zu speichern, zu signieren und zu drucken (als PDF).

###Maven Build
Um den Maven Build-Prozess eigenständig durchzuführen, sind in den jeweiligen Verzeichnissen folgende Schritte notwendig:

      regkassen-core: mvn install
      regkassen-democashbox: mvn install
      
In den Verzeichnissen regkassen-democashbox, regkassen-verification befinden sich nach dem erfolgreichen Build-Prozess die JAR Dateien (im Unterverzeichnis "target"), die zum Ausführen benötigt werden (siehe Punkte zur Verwendung des Demo-Codes weiter oben).

#Erläuterungen zur Detailspezifikation der Verordnung/FAQs
Werden laufend ergänzt:
https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erläuterungen-FAQ

# Impressum
Informationen zu A-SIT und A-SIT Plus unter http://www.a-sit.at

A-SIT Plus GmbH
A-1030 Wien,
Seidlgasse 22 / 9
1030 Wien
FN 436920 f,
Handelsgericht Wien


