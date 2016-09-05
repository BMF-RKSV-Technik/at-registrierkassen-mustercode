
# Übersicht
Dieses Projekt dient zur Behandlung technischer Sachverhalte der RKSV. Wir bitten daher um Verständnis, dass rechtliche/organisatorische Themen im Allgemeinen nicht beantwortet werden können. Für die Beantwortung solcher Fragen bitten wir Sie die Informationen des BMFs heranzuziehen (siehe https://www.bmf.gv.at/top-themen/Registrierkassen.html). Sollte Ihr Anliegen dort nicht behandelt sein, steht Ihnen unter https://www.bmf.gv.at/kontakt.html eine Möglichkeit zur Verfügung, Ihre rechtliche oder organisatorische Frage schriftlich im BMF einzubringen. Nutzen Sie dazu die Option “Sonstige Anfrage” und fügen Sie Ihrem Anliegen den deutlichen Hinweis hinzu, dass es sich um ein Thema im Bereich Registrierkasse handelt. Bitte auch die Kontaktinformationen, die am Ende dieses Dokuments genannt werden, beachten.
 
Ein weiterer Hinweis bezüglich den “Issues” in diesem Projekt: Offizielle – mit dem BMF abgestimmte – Aussagen werden nur von den Benutzern der Organisation A-SIT Plus (pteufl, tzefferer, asitplus-pteufl) getätigt. Für die Korrektheit der Aussagen anderer Benutzer kann keine Garantie übernommen werden.

Dieses Dokument ist wie folgt organisiert:
 
 - **Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)**: In diesem Abschnitt werden Informationen zum gleichnamigen Dokument gegeben. Dieses Dokument beschreibt RKSV-relevante Prozesse im Detail und stellt neben dem Muster-Code die Grundinformation für die Umsetzung in einer Kasse zur Verfügung.
 - **Prüftool**: Das Prüftool ermöglicht es den Kassenherstellern die RKSV-Konformität der Kasse zu überprüfen.  **ES WIRD DRINGEND EMPFOHLEN DIE RKSV-KONFORME IMPLEMENTIERUNG EINER KASSE MIT DEN ZUR VERFÜGUNG GESTELLTEN TOOLS ZU ÜBERPRÜFEN, BEVOR DIE IMPLEMENTIERUNG DEN KUNDEN ÜBERGEBEN WIRD BZW. DATEN IM FINANZONLINE REGISTRIERT WERDEN.**
 - **Muster-Code**: Dieser Abschnitt gibt die relvanten Informationen zum Muster-Code der die RKSV-relevanten Elemente einer Kasse demonstriert. Der Muster-Code setzt dabei die Prozesse des Dokuments "Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)" um.
 - **Testfälle**: Dieser Abschnitt stellt Testfälle für die Überprüfung der Implementierung einer Kasse zur Verfügung.
 - **Kontakt/Fragen**: Kontaktinformationen für Detailfragen, die nicht im Rahmen dieses Projekts beantwortet werden können.

# Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)
In Zusammenarbeit zwischen dem BMF und A-SIT Plus wurde das Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)* erstellt. Es enthält Festlegungen in technischen Detailfragen zur RKSV auf Prozessebene und Klarstellungen bzw. Ergänzungen im Bereich der Mustercode-Beispiele. 

**Releases**:

 - **Version 1.2 (05.09.2016)**: [Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV) V1.2] (https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases/download/1.2-DOK/2016-09-05-Detailfragen-RKSV-V1.2.pdf): 
	 - **Liste der Änderungen**: Es wurden die bei V1.1. unter "Bekannte Probleme" angemerkten Fehler behoben und der Algorithmus zur Berechnung der Prüfsumme des AES-Schlüssels für die manuelle Übermittlung im FinanzOnline hinzugefügt.
		 - siehe Change-Log im Dokument
		 - [Diff (Änderungsmarkierungen) von V1.1 zu V1.2](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases/download/1.2-DOK/2016-05-09-Diff-Detailfragen-RKSV-V1.1-V1.2.pdf)
 - **Version 1.1 (11.03.2016)**: [Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV) V1.1] (https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases/download/1.1-DOK/2016-03-11-Detailfragen-RKSV-V1.1.pdf): 
	 - **Liste der Änderungen**: Im Wesentlichen wurden die Kapitel um die ersten Kassen-Testfälle ergänzt, sowie diverse Fehlerbehebungen vorgenommen. Details zu den Änderungen:
		 - siehe Change-Log im Dokument
		 - [Diff (Änderungsmarkierungen) von V1.0 zu V1.1](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases/download/1.1-DOK/2016-03-11-Diff-Detailfragen-RKSV-V1.0-V1.1.pdf)
	 - **Bekannte Probleme**:
		 - https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/48
		 - https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/52
		 - https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/54
		 - https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/56
		 - https://github.com/a-sit-plus/at-registrierkassen-mustercode/issues/77
 - **Version 1.0 (19.02.2016)**:
	 - 	[Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV) V1.0](https://github.com/a-sit-plus/at-registrierkassen-mustercode/files/137544/2016-02-18-Detailfragen-RKSV-V1.0.pdf)

# Prüftool
Das Prüftool ermöglicht es den Kassenherstellern die RKSV-Konformität der Kasse zu überprüfen. Dabei können sowohl Einzelbelege als auch der RKSV-konforme Export des DEPs geprüft werden. Mit Version 0.7. liefert das Prüftool für die Belege die gleichen Ergebnisse wie sie auch im FinanzOnline zur Verfügung stehen bzw. über das Web-Service zurückgegeben werden. Es muss dabei berücksichtigt werden, dass das Prüftool keinen Zugriff auf die Daten im FinanzOnline hat und daher nicht den Status der Registrierung der Signatur- bzw. Siegelerstellungseinheit und der Kasse überprüfen kann. Im Prüftool wird immer davon ausgegangen, dass der Status im FinanzOnline korrekt ist. Es wird daher rein die Korrektheit des Belegs bzw. des DEPs geprüft.

***Change-Log*** 

 - 05.09.2016: Release 0.7 veröffentlicht: Das gesamte Prüftool wurde geändert, der Prüfkern liefert nun die gleichen Ergebnisse wie sie auch im FinanzOnline oder über das WebService zur Verfügung stehen.

***Verwendung des Prüftools***
Download und entpacken von `regkassen-demo-release-0.7.zip` (siehe [https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases)).

***DEP-Export Format***
Mit dieser Variante kann das gesamte RKSV-DEP überprüft werden.

    java -jar regkassen-verification-depformat-0.7.jar -i DEP-EXPORT-FILE -c CRYPTOGRAPHIC-MATERIAL-FILE -o OUTPUT_DIR
	         
Wobei

 - der Parameter **DEP-EXPORT-FILE**, der der im vorigen Beispiel erstellten `dep-export.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - der Parameter **CRYPTOGRAPHIC-MATERIAL-FILE**, der der im vorigen Beispiel erstellen `cryptographicMaterialContainer.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - Der Parameter **-o OUTPUT_DIR** gibt ein Verzeichnis an, in dem die Detailergebnisse des Prüftools gespeichert werden.

Es werden dabei folgende Prüfungen durchgeführt:

 - **DEP-Prüfungen**: Es wird die gültige Verkettung der Belege, die korrekte Entwicklung des Umsatzzählers sowie die korrekte Belegabfolge überprüft.
 - **Einzelbelege**: Pro Beleg erfolgt eine detallierte Prüfung der Gültigkeit des Belegs. Es werden dabei die gleichen Prüfungen wie im FinanzOnline durchgeführt und das Ergebnis im gleichen Format aufbereitet. Das Prüftool hat keinen Zugriff auf FinanzOnline, daher wird für den Registrierungstatus der Kasse und der verwendeten Siegel- bzw. Signaturerstellungseinheit immer der korrekte Wert angenommen. Die Detailergebnisse werden im angegeben Verzeichnis (**OUTPUT_DIR**) abgelegt. Dieses Verzeichnis enthält dabei für jeden Beleg die Detailergebnisse, wobei **N** der Nummerierung der Input-Belege im DEP-Export entspricht. Kommt es zu einem Fehler bei der Einzelbelegprüfung, werden die restlichen Belege NICHT mehr überprüft.
	 - **M-DEP.json**: In dieser Datei werden die Detailergebnisse der DEP-Prüfung (Verkettung, Entwicklung Umsatzzähler, Abfolge der Belege) ausgegeben. (**M** entspricht einem Index der den Gruppen im DEP-Export entspricht, startend mit 1. Im Normalfall gibt es nur eine Gruppe im DEP, daher auch nur eine Ergebnis-Datei).
	 - **N_cashbox.json**: Dieses Ergebnis enspricht dem Ergebnis, das in FinanzOnline visuell aufbereitet wird und als Datei zu beziehen ist. Ebenso wird das Prüfergebnis im gleichen Format vom Web-Service für die Kassen zur Verfügung gestellt. Es werden nur die fehlerhaften Prüfergebnisse angezeigt. (**N** entspricht der Belegnummer im DEP, startend mit 1. Sind mehrere Beleggruppen im DEP wird die Nummerierung der Belege nicht zurückgesetzt.)
	 - **N_cashbox_full.json**: Es werden auch die positiven Prüfergebnisse angezeigt. Der Hersteller hat hier die Möglichkeit das vollständige Prüfergebnis einzusehen und alle durchgeführten Prüfungen zu erkennen. (**N** entspricht der Belegnummer im DEP, startend mit 1. Sind mehrere Beleggruppen im DEP wird die Nummerierung der Belege nicht zurückgesetzt.)
	 - **N_app.json**: Diese Ergebnis entspricht dem Ergebnis, das die App (Belegcheck) von FinanzOnline bekommt. Für Detailanalysen hat es keine wirkliche Relevanz, es zeigt aber wie etwaige Fehler in der App repräsentiert werden. (**N** entspricht der Belegnummer im DEP, startend mit 1. Sind mehrere Beleggruppen im DEP wird die Nummerierung der Belege nicht zurückgesetzt.)


***QR-Code-Repräsentation eines einzelnen Belegs oder mehrerer Belege***
In dieser Variante werden Einzelbelege auf Ihre Gültigkeit überprüft. Die Ergebnisse entsprechen jenen der Einzelbelegprüfung die für die Prüfung des RKSV-konformen DEPs zur Verfügung stehen. **ACHTUNG**: Sollten sich mehrere Belege in der Datei **QR-CODE-REP-FILE** befinden, so wird deren Verkettung **NICHT** überprüft. Diese Prüfung wird nur bei der DEP-Export-Format Prüfung durchgeführt.

    java -jar regkassen-verification-receipts-0.7.jar -i QR-CODE-REP-FILE -c CRYPTOGRAPHIC-MATERIAL-FILE -o OUTPUT_DIR

Wobei

 - der Parameter **QR-CODE-REP-FILE**, der der im vorigen Beispiel erstellen `qr-code-rep.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - der Parameter **CRYPTOGRAPHIC-MATERIAL-FILE**, der der im vorigen Beispiel erstellen `cryptographicMaterialContainer.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - Der Parameter **-o OUTPUT_DIR** gibt ein Verzeichnis an, in dem die Detailergebnisse des Prüftools gespeichert werden.

Die Ergebnisse ensprechen dem Format das unter Einzelbelegprüfung für den RKSV-konformen DEP-Export beschrieben ist.


# Muster-Code
Dieses Projekt stellt Demo-Code als Begleitung zur [Registrierkassensicherheitsverordnung (RKSV)](https://www.bmf.gv.at/steuern/Registrierkassensicherheitsverordnung.html) zur Verfügung und wurde in der Zusammenarbeit zwischen BMF und A-SIT Plus erstellt. Der Demo-Code zeigt

* wie die wesentlichen Elemente der Detailspezifikation der Verordnung in Software implementiert werden können,
* gibt zusätzliche Erläuterungen zu Aspekten der Detailspezifikation die noch Interpretationsspielraum zulassen,
* und stellt Prüfwerkzeuge zur Verfügung, die es erleichtern die korrekte Umsetzung einer Registrierkasse im Sinne der Detailspezifikation zu überprüfen (diese Werkzeuge werden ständig erweitert, siehe ChangeLog).

In diesem Projekt werden vorwiegend technische Aspekte der Verordnung betrachtet. Die Informationen und der Code werden laufend erweitert und mit typischen Fragen/Antworten ergänzt.

Die Versionen 0.1 bis 0.7 demonstrieren wie mit den unterschiedlichen Elementen (Signatur, QR-Code etc.) umgegangen werden muss. Obwohl hier nach bestem Gewissen vorgegangen wurde, kann keine GARANTIE für die korrekte Funktionsweise übernommen werden. Die Prüfung der Korrektheit erfolgte mit den unabhängigen Prüfwerkzeugen die für die Version 0.7. in der Einzelbelegprüfung qualitätsgesichert wurden (detaillierte Tests, Abstimmung mit Ergebnissen im FinanzOnline).

Diese Projektseite verwendet Deutsch als Sprache. In den textuellen Ergänzungen im Source Code wird Englisch verwendet.

Der Muster-Coder wird unter der Apache 2.0 Lizenz zur Verfügung gestellt. (http://www.apache.org/licenses/LICENSE-2.0). Der Code für die Prüftools wird nicht veröffentlicht. Die Verwendung der Prüftools ist natürlich frei möglich.

Alle verwendeten Dritt-Bibliotheken und deren Lizenzen sind in den Maven Build Dateien (pom.xml) der einzelnen Module ersichtlich und auf der folgenden WIKI Seite zusammengefasst:

https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Lizenzen-Dritt-Bibiliotheken

***Change-Log***
Die Change-Logs bis zu Release 0.6 wurden aus Gründen der Übersichtlichkeit archiviert (siehe [Archiv im Wiki](https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Changelog-Archiv-bis-02.09.2016)).

 - **05.09.2016**: Release 0.7 veröffentlicht
	 - **Änderungen**:
		 - **Testfälle**: Nullbelege müssen eine gültige Signatur haben. Dies wurde in den Testfällen für den Beispiel Code entsprechend korrigiert.
		 - **Umsatzzähler mit Länge ungleich 8**: Der Muster-Code zeigt nun die Aufbereitung eines Umsatzzählers ungleich der Länge 8. Mit dem Parameter "l" kann die Länge des Umsatzzählers für die Erstellung der Beispielbelege definiert werden.
		 - **Organisation des Codes**: Diverse Pakete wurden umbenannt bzw. in andere Teile des Codes verschoben.
		
***Verwendung des Democodes und der Demokasse***
Neben dem Source Code wird auch immer eine ZIP Datei der ausführbaren Dateien zur Verfügung gestellt. Die neueste Version ist immer unter [Releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases) zu finden. Für das Ausführen der Demokasse sind folgende Voraussetzungen nötig:

* *Java VM*: Es wird eine aktuelle Java VM (JRE ausreichend) mit Version >= 1.7 benötigt.
* *Kryptographie*: Der Registrierkassen-Demo-Code verwendet starke Kryptographie (z.B. AES mit 256 bit Schlüssel), der mit den Standard-Export Policies der Java VM nicht ausgeführt werden kann. Es muss daher die "Unlimited Strength Policy" von Oracle installiert werden. Siehe: [http://www.oracle.com/technetwork/java/javase/downloads/index.html](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

Um die Demokasse zu verwenden, wird wie folgt vorgegangen: 
Download und entpacken von `regkassen-demo-release-0.7.zip` (siehe https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases).

Ausführen der Demokasse für die Abarbeitung der integrierten Testfälle mit
`java -jar regkassen-demo-0.7.jar -o OUTPUT_DIR -v -c -l 8`

**Parameter**: 

 - Der optionale Parameter **-o OUTPUT_DIR** gibt ein Verzeichnis an, in dem die vom Demo-Code erstellten Daten/Belege gespeichert werden. Wenn die Option **-o** nicht angegeben wird, werden die Ergebnisse in ein automatisch generiertes Verzeichnis im Arbeitsverzeichnis gespeichert. Format für das erstellte Verzeichnis: `CashBoxDemoOutputyyyy-MM-dd'T'HH-mm-ss` wobei `yyyy-MM-dd'T'HH-mm-ss` der aktuellen Zeit im angegebenen Format entspricht.
 - **-v (verbose)**: Der optionale Parameter **-v** gibt an ob die generierten Daten (Belege, DEP-Export etc.) auch über STDOUT ausgegeben werden. Ist **-v** nicht angegeben, so werden die Daten nur in das Output Verzeichnis geschrieben.
 - **-c (closed-system)**: Der optionale Parameter **-c** gibt an, ob es sich um ein geschlossenes System handelt. In diesem Fall werden statt der Seriennummer des Zertifikats der Ordnungsbegriff des Unternehmens und das Identifikationsmerkmal des verwendeten Schlüssels in den erstellten Belegen verwendet. Außerdem werden statt den X509-Zertifikaten öffentliche Schlüssel für die Signaturprüfung zur Verfügung gestellt (siehe Datei `cryptographicMaterialContainer.json`).
 - Der optionale Parameter **-l TURNOVER-COUNTER-LENGTH** gibt an wieviele Bytes für die Kodierung des Umsatzzählers verwendet werden sollen. Wird der Parameter nicht angegeben oder wird ein Wert kleiner 5 oder größer 8 angegeben so werden 8 bytes für die Kodierung des Umsatzzählers verwendet.
 
**Das Output-Verzeichnis enthält folgende Dateien/Verzeichnisse**:
Für jeden Test-Fall wird ein eigenes Verzeichnis angelegt, das den Namen des Testfalls erhält.  In diesem Verzeichnis werden unterschiedliche Dateien/Verzeichnisse gespeichert. Die folgenden Dateien bzw. deren Formate haben zwar für eine produktive Kasse (mit Ausnahme des DEP-Exports) keine Bedeutung, allerdings spielen sie bei der Überprüfung der Implementierung der Kasse eine wichtige Rolle, da die Dateien vom Prüftool verwendet werden, um die Testfälle einer Kasse und vor allem deren Abdeckung prüfen zu können (Abdeckung in Version 0.7 des Prüftools geplant):

 - **dep-export.json (Datei)**: In dieser Datei werden die die erstellten Belege im DEP Export Format (Detailspezifikation, Abs 3) gespeichert.
Änderungen zu Release 0.5: Der Dateiname wurde von `dep-export.txt` auf `dep-export.json` geändert und der Export erhält keine Zertifikate (Signaturzertifikate,  Zertifikatsketten) mehr. Diese wurden in Übereinstimmung mit dem Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)*  aus dem RKSV-konformen Export des DEPs entfernt. Diese können optional hinzugefügt werden, sind aber nicht gefordert, da vor allem bei der Verwendung von mehreren Signatureinrichtungen die Gruppierung nach Signaturzertifikaten die Datenmenge des DEP-Exports signifikant vergrößeren würde.
Weitere Details dazu: siehe Abschnitt 7.2.1 im Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)*.
 - **cryptographicMaterialContainer.json (Datei)**: Änderungen zu Release 0.5: Diese Datei ersetzt die Dateien `aesKeyBase64.txt` und `signatureCertificates.txt`. Die Informationen der Datei `signatureCertificateChains.txt` werden ab Version 0.6 nicht mehr zur Verfügung gestellt.
Weitere Details dazu: siehe Abschnitt 7.2.2 im Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)*.

Die folgenden Dateien dienen nur zur Demonstrationszwecken und haben für das Prüftool (auch ab Version 0.7) keine Relevanz:

 - **qr-code-rep.json (Datei)**: Die textuelle Repräsentation der maschinenlesbaren QR-Codes als JSON-Array. Eine Zeile der Datei entspricht der QR-Code Repräsentation eines Belegs.
 -  **ocr-code-rep.json (Datei)**: Die textuelle Representation der maschinenlesbaren OCR-Codes als JSON-Array. Eine Zeile der Datei entspricht der OCR-Code Repräsentation eines Belegs.
 - **qr-code-dir-pdf (Verzeichnis)**: Einfache Demo-PDF-Belege die mit dem QR-Code bedruckt wurden.
 - **ocr-code-dir-pdf (Verzeichnis)**: Einfache Demo-PDF-Belege die mit dem OCR-Code bedruckt wurden.

Ein Beispiel für den Output ist auch direkt ohne dem Ausführen des Demo-Codes verfügbar: `example-output-0.7.zip` (siehe [https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases)).

***BUILD Prozess***
Um den Maven Build-Prozess eigenständig durchzuführen, sind in den jeweiligen Verzeichnissen folgende Schritte notwendig:

      regkassen-core: mvn install
      regkassen-democashbox: mvn install
      
In den Verzeichnissen `regkassen-democashbox`, `regkassen-verification` befinden sich nach dem erfolgreichen Build-Prozess die JAR Dateien (im Unterverzeichnis "target"), die zum Ausführen benötigt werden (siehe Punkte zur Verwendung des Demo-Codes weiter oben).
                    
##Testfälle
Die Tesfälle sind im Mustercode der Demokasse integriert bzw. können durch Download und Entpacken von `regkassen-test-cases-0.7.zip` (siehe [https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases)) bezogen werden.
Eine detallierte Beschreibung der Testfälle befindet sich im Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)*. Diese Beschreibung umfasst:

 - Die Beschreibung der verwendeten Datenformate der Testfälle für eine automatisierte Verarbeitung in einem Kassensystem, um die definierten Testbelege und Belegabläufe erstellen zu können.
 - Erklärungen zu den unterschiedlichen Testfällen und deren Hintergründe.

 
#Kontakt/Fragen
Es wurde dazu eine Projektseite von der WKO eingerichtet. Es ist dazu eine Registrierung bei der WKO notwendig.

[Projektseite der WKO](https://communities.wko.at/Kassensoftware/default.aspx)

Etwaige Fragen sollten dort im Forum gestellt werden, um eine möglichst effizient die Beantwortung durchführen zu können. 

[Forum der WKO](https://communities.wko.at/Kassensoftware/Lists/Forum/)

Ausgewählte Fragen/Antworten werden aus diesem Forum übernommen und auf der hier verfügbaren WIKI Seite eingetragen.

[WIKI](https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erl%C3%A4uterungen-FAQ)

# Impressum
Informationen zu A-SIT und A-SIT Plus unter http://www.a-sit.at

A-SIT Plus GmbH
A-1030 Wien,
Seidlgasse 22 / 9
1030 Wien
FN 436920 f,
Handelsgericht Wien
