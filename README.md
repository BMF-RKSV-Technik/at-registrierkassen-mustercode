
# Bekanntgabe des BMF zur Registrierung von Registrierkassen über FinanzOnline

**Das BMF gibt folgende Information zur Registrierung von Registrierkassen über FinanzOnline bekannt:**

Um Fehleingaben bei der Erfassung von AES-Schlüsseln bei der Registrierung von Registrierkassen über FinanzOnline vermeiden zu helfen, führt das BMF ein Prüfwertverfahren für die Erfassung der AES-Schlüssel über FinanzOnline ein, das ab Ende August 2016 zur Verfügung stehen wird. Das Prüfwertverfahren ist optional und besteht aus einem vierstelligen Prüfwert, der nach vorgegebenen Regeln (Berechnungsalgorithmus) aus dem AES-Schlüssel ermittelt werden kann. Wird der Prüfwert nach dem selben Berechnungsalgorithmus auch von der Registrierkasse ermittelt und vom Unternehmer zusätzlich zum AES-Schlüssel über FinanzOnline erfasst, stellt FinanzOnline durch eine Vergleichsrechnung sicher, dass der AES-Schlüssel fehlerfrei über FinanzOnline erfasst wurde.

Hinweis für Softwaretechniker: die Verwendung des SHA256-Hash-Wertes begründet sich in der Tatsache, dass die Kassensoftware die dafür benötigten Softwarebibliotheken bereits im Einsatz hat. Auch die Extraktion von einer gegebenen Anzahl von Bytes aus dem berechneten Hash-Wert muss bereits im Rahmen der RKSV-konformen Umsetzung vorhanden sein. Die Aufwände für die Implementierung sollen damit minimal gehalten werden.

Berechnungsalgorithmus Prüfwert für AES-Schlüssel:

1. **Eingabewerte:**
  a. **base64AESKey:** BASE64-kodierter AES Schlüssel, mit dem die Kasse initialisiert wurde und der im FinanzOnline gemeldet werden soll.
  b. **N:** Die Anzahl der Bytes, die vom Hash-Wert extrahiert werden. Es wird **N=3** festgelegt.
2. **Berechnung der Prüfsumme:**
  a. **Hashberechnung:** SHA256-Hash-Wert-Berechnung von **base64AESKey -> sha256hash** (Byte Array der Länge 32)
  b. Extraktion der ersten **N** Bytes aus **sha256hash -> sha256hashNbytes**
(Byte Array der Länge **N**)
  c. BASE64-Kodierung von **sha256hashNbytes -> base64sha256hashNbytes**
  d. Entfernen aller „=“ Zeichen aus **base64sha256hashNbytes -> valSumCalc**
3. **Output:**
   a. **valSumCalc:** Prüfwert der vom Unternehmer im FinanzOnline eingegeben werden kann. FinanzOnline verwendet den gleichen Algorithmus für die Berechnung des Prüfwerts und informiert den Unternehmer, wenn der berechnete und der eingegebene Wert nicht identisch sind.

Code-Snippet, **N=3**:

```
public static boolean checkValSum(int N, String base64AESKey, String userCheckSum) throws NoSuchAlgorithmException {
    String calculatedCheckSum = calcCheckSumFromKey(base64AESKey, N);
    return calculatedCheckSum.equals(userCheckSum);
}

public static String calcCheckSumFromKey(String base64AESKey, int N) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");

    byte[] sha256hash  = md.digest(base64AESKey.getBytes());
    byte[] sha256hashNbytes = new byte[N];

    System.arraycopy(sha256hash , 0, sha256hashNbytes, 0, N);

    String base64sha256hashNbytes = CashBoxUtils.base64Encode(sha256hashNbytes, false);
    String valSumCalc = base64sha256hashNbytes.replace("=", "");

    return valSumCalc;
}

```

# Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)
In Zusammenarbeit zwischen dem BMF und A-SIT Plus wurde das Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)* erstellt. Es enthält Festlegungen in technischen Detailfragen zur RKSV auf Prozessebene und Klarstellungen bzw. Ergänzungen im Bereich der Mustercode-Beispiele. 

**Releases**:

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

# Wichtige Information zu diesem Projekt
Dieses Projekt dient zur Behandlung technischer Sachverhalte der RKSV. Wir bitten daher um Verständnis, dass rechtliche/organisatorische Themen im Allgemeinen nicht beantwortet werden können. Für die Beantwortung solcher Fragen bitten wir Sie die Informationen des BMFs heranzuziehen (siehe https://www.bmf.gv.at/top-themen/Registrierkassen.html). Sollte Ihr Anliegen dort nicht behandelt sein, steht Ihnen unter https://www.bmf.gv.at/kontakt.html eine Möglichkeit zur Verfügung, Ihre rechtliche oder organisatorische Frage schriftlich im BMF einzubringen. Nutzen Sie dazu die Option “Sonstige Anfrage” und fügen Sie Ihrem Anliegen den deutlichen Hinweis hinzu, dass es sich um ein Thema im Bereich Registrierkasse handelt.
 
Ein weiterer Hinweis bezüglich den “Issues” in diesem Projekt: Offizielle – mit dem BMF abgestimmte – Aussagen werden nur vom Benutzer **asitplus-pteufl** getätigt. Für die Korrektheit der Aussagen anderer Benutzer kann keine Garantie übernommen werden.

# Muster-Code, Prüftools, Change Log
Die Change-Logs bis zu Release 0.5 wurden aus Gründen der Übersichtlichkeit archiviert (siehe [Archiv im Wiki](https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Changelog-Archiv-bis-11.03.2016)).

 - **11.03.2016**: Release 0.6 veröffentlicht
	 - **Korrekturen**:
		 - **Seriennummer im Zertifikat in Hexadezimal Darstellung**: Die Seriennummer für offene Systeme wird nun in Übereinstimmung mit dem Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)* hexadezimal dargestellt.
		 - **Korrekte Prüfung von Belegen** die die Hexadezimal-Darstellung für das Zertifikat verwenden. Das Prüftool bis zu Version 0.5 konnte nur Belege mit Seriennummern im Integer-Format prüfen. Dies wurde mit Version 0.6 behoben.
		 - **Unlimited Strength Policy**: In allen Programmen (Demo-Kasse, Prüftools) wird nun eine Überprüfung auf die Verfügbarkeit der Java Unlimited Strength Policies (siehe weiter unten) durchgeführt. Beim Nichtvorhandensein dieser Policy-Dateien wird das jeweilige Programm mit einer Fehlermeldung beendet. 
	 - **Features**:
		 - **Demo-Code allgemein**: Der Muster-Code wurde mit dem Fokus auf *Demonstration* geändert, um hier eine klare Darstellung der wesentlichen Elemente der Belegerstellung zu erhalten. Der Code für die Behandlung der gültigen Belegabfolgen (Zustandsänderungen) bzw. die Vermischung zwischen Demo-Code für die Kassenfunktion und Code der für die Erstellung von Demonstrationsbelegen (z.B. Wahrscheinlichkeit für eine ausgefallene Signatureinrichtung und die korrekte Behandlung der Belege davor/danach) wurde entfernt. Ebenso wird, wie in den vorherigen Versionen, keine Fehlerbehandlung bzw. Logging im Code durchgeführt. Der Fokus liegt im Wesentlichen darauf die Kernelmente der RKSV zu demonstrieren (Signature, DEP).
		 - **Basis Test-Suites**: Mit Version 0.6 stehen die ersten Basis-Testsuites für die Überprüfung der Kassenfunktion zur Verfügung. Die Basis-Testsuites wurden in die Demo-Kasse integriert und werden beim Ausführen abgearbeitet. Die Test-Suites bzw. deren Format werden natürlich auch ergänzend dazu zur Verfügung gestellt und erlauben den Herstellern definierte Testabläufe der Systeme durchzuführen (Details weiter unten). Das Prüftool in Version 0.6 kann die Ergebnisse dieser Testfälle wie bisher prüfen. Es ist aber erst für die Version 0.7 des Prüftools geplant, weitere Funktionen zu integrieren die es auch ermöglichen die Vollständigkeit der überprüften Testfälle zu überprüfen. D.h. im Moment kann zwar die Korrektheit einer Belegkette geprüft werden, das Prüftool überprüft aber noch nicht, ob diese Kette auch dem vorgegebenen Testfall entspricht bzw. ob alle Testfälle ausgeführt wurden (Abdeckung der Testfälle). Diese Funktion wird mit einer detaillierten Überarbeitung des Prüftools einhergehen (Qualitätssicherung, Resultate in strukturiertem Format). Mit Version 0.7 werden auch weitere Testfälle integriert die sich auf die unterschiedlichen Verwendungszwecke der Kassen fokussieren (Inbetriebnahme, Abmeldung, Beschädigung etc.).
		 - **Geschlossene/Offene Systeme**: In Version 0.6. werden nun beide Systemtypen unterstützt. Der wesentliche Aspekte dabei ist die unterschiedliche Behandlung der "Seriennummer der Zertifikats" im Beleg.
		 - **Signatureinrichtungen**: Der Demo-Code und die Testfälle berücksichtigen nun auch die Pool-artige Verwendung von mehreren Signatureinrichtungen pro Kasse.
		 - **DEP-Export-Format**: In Übereinstimmung mit dem Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)* wurden die Zertifikate (Signaturzertifikate, Zertifikatsketten) aus dem RKSV-konformen Export des DEPs entfernt. Diese können optional hinzugefügt werden, sind aber nicht gefordert, da vor allem bei der Verwendung von mehreren Signatureinrichtungen die Gruppierung nach Signaturzertifikaten die Datenmenge des DEP-Exports signifikant vergrößern würde.

# Überblick
Dieses Projekt stellt Demo-Code als Begleitung zur [Registrierkassensicherheitsverordnung (RKSV)](https://www.bmf.gv.at/steuern/Registrierkassensicherheitsverordnung.html) zur Verfügung. Der Demo-Code zeigt

* wie die wesentlichen Elemente der Detailspezifikation der Verordnung in Software implementiert werden können,
* gibt zusätzliche Erläuterungen zu Aspekten der Detailspezifikation die noch Interpretationsspielraum zulassen,
* und stellt Prüfwerkzeuge zur Verfügung, die es erleichtern die korrekte Umsetzung einer Registrierkasse im Sinne der Detailspezifikation zu überprüfen (diese Werkzeuge werden ständig erweitert, siehe ChangeLog).

In diesem Projekt werden vorwiegend technische Aspekte der Verordnung betrachtet. Die Informationen und der Code werden laufend erweitert und mit typischen Fragen/Antworten ergänzt.

## Wichtige Anmerkungen/Einschränkungen
* **Wichtige Anmerkung**: Die Versionen 0.1 bis 0.6 demonstrieren wie mit den unterschiedlichen Elementen (Signatur, QR-Code etc.) umgegangen werden muss. Obwohl hier nach bestem Gewissen vorgegangen wurde, kann keine GARANTIE für die korrekte Funktionsweise übernommen werden. Um hier in den nächsten Versionen mehr Klarheit bieten zu können, werden vom aktuellen Code unabhängige Prüfwerkzeuge geschaffen. Diese ermöglichen den Kassaherstellern die jeweiligen Produkte (und auch diesen Demo-Code) so weit wie möglich auf das korrekte Verhalten mit Relevanz für die Verordnung zu überprüfen.
* **Sprache**: Diese Projektseite verwendet Deutsch als Sprache. In den textuellen Ergänzungen im Source Code wird Englisch verwendet.

##Weiteres Vorgehen
Mit Version 0.6 stehen die Strukturen für das Abarbeiten von Testfällen, die Formate der Testfälle, sowie die ersten Basistestfälle zur Verfügung. In Version 0.7 wird der Fokus auf die Bereitstellung von weiteren Testfällen und vor allem die Prüfung dieser Testfälle gelegt. Das Prüftool wird dahingehend erweitert, dass das Erkennen von nicht-vollständigen Test-Suites möglich ist,  aber auch weitere Detailprüfungen durchgeführt werden können. 

##Kontakt/Fragen
Es wurde dazu eine Projektseite von der WKO eingerichtet. Es ist dazu eine Registrierung bei der WKO notwendig.

[Projektseite der WKO](https://communities.wko.at/Kassensoftware/default.aspx)

Etwaige Fragen sollten dort im Forum gestellt werden, um eine möglichst effizient die Beantwortung durchführen zu können. 

[Forum der WKO](https://communities.wko.at/Kassensoftware/Lists/Forum/)

Ausgewählte Fragen/Antworten werden aus diesem Forum übernommen und auf der hier verfügbaren WIKI Seite eingetragen.

[WIKI](https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erl%C3%A4uterungen-FAQ)

## Lizenz
Der gesamte Code wird unter der Apache 2.0 Lizenz zur Verfügung gestellt. (http://www.apache.org/licenses/LICENSE-2.0).
Der Code für die Prüftools wird nicht veröffentlicht. Die Verwendung der Prüftools ist natürlich frei möglich.

Alle verwendeten Dritt-Bibliotheken und deren Lizenzen sind in den Maven Build Dateien (pom.xml) der einzelnen Module ersichtlich und auf der folgenden WIKI Seite zusammengefasst:

https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Lizenzen-Dritt-Bibiliotheken

# Verwendung des Democodes und der Demokassa

##Ausführen des Codes (Verwenden der Downloadpakete)
Neben dem Source Code wird auch immer eine ZIP Datei der ausführbaren Dateien zur Verfügung gestellt. Die neueste Version ist immer unter [Releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases) zu finden.

###Voraussetzungen
* *Java VM*: Es wird eine aktuelle Java VM (JRE ausreichend) mit Version >= 1.7 benötigt.
* *Kryptographie*: Der Registrierkassen-Demo-Code verwendet starke Kryptographie (z.B. AES mit 256 bit Schlüssel), der mit den Standard-Export Policies der Java VM nicht ausgeführt werden kann. Es muss daher die "Unlimited Strength Policy" von Oracle installiert werden. Siehe: [http://www.oracle.com/technetwork/java/javase/downloads/index.html](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

###Verwendung des Demo-Codes - Demokassa
Download und entpacken von `regkassen-demo-release-0.6.zip` (siehe https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases).

Ausführen der Demokasse für die Abarbeitung der integrierten Testfälle mit
`java -jar regkassen-demo-0.6.jar -o OUTPUT_DIR -v -c`

**Parameter**: 

 - Der optionale Parameter **-o OUTPUT_DIR** gibt ein Verzeichnis an, in dem die vom Demo-Code erstellten Daten/Belege gespeichert werden. Wenn die Option **-o** nicht angegeben wird, werden die Ergebnisse in ein automatisch generiertes Verzeichnis im Arbeitsverzeichnis gespeichert. Format für das erstellte Verzeichnis: `CashBoxDemoOutputyyyy-MM-dd'T'HH-mm-ss` wobei `yyyy-MM-dd'T'HH-mm-ss` der aktuellen Zeit im angegebenen Format entspricht.
 - **-v (verbose)**: Der optionale Parameter **-v** gibt an ob die generierten Daten (Belege, DEP-Export etc.) auch über STDOUT ausgegeben werden. Ist **-v** nicht angegeben, so werden die Daten nur in das Output Verzeichnis geschrieben.
 - **-c (closed-system)**: Der optionale Parameter **-c** gibt an, ob es sich um ein geschlossenes System handelt. In diesem Fall werden statt der Seriennummer des Zertifikats der Ordnungsbegriff des Unternehmens und das Identifikationsmerkmal des verwendeten Schlüssels in den erstellten Belegen verwendet. Außerdem werden statt den X509-Zertifikaten öffentliche Schlüssel für die Signaturprüfung zur Verfügung gestellt (siehe Datei `cryptographicMaterialContainer.json`).
 
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

Ein Beispiel für den Output ist auch direkt ohne dem Ausführen des Demo-Codes verfügbar: `example-output-0.6.zip` (siehe [https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases)).

###Verwendung des Prüfwerkzeugs
Download und entpacken von `regkassen-demo-release-0.6.zip` (siehe [https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases)).

**DEP-Export Format**

    java -jar regkassen-verification-depformat-0.6.jar -i DEP-EXPORT-FILE -c CRYPTOGRAPHIC-MATERIAL-FILE
	         
Wobei

 - der Parameter **DEP-EXPORT-FILE**, der der im vorigen Beispiel erstellten `dep-export.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - der Parameter **CRYPTOGRAPHIC-MATERIAL-FILE**, der der im vorigen Beispiel erstellen `cryptographicMaterialContainer.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.

**QR-Code-Repräsentation eines einzelnen Belegs oder mehrerer Belege**

    java -jar regkassen-verification-receipts-0.6.jar -i QR-CODE-REP-FILE -c CRYPTOGRAPHIC-MATERIAL-FILE

Wobei

 - der Parameter **QR-CODE-REP-FILE**, der der im vorigen Beispiel erstellen `qr-code-rep.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - der Parameter **CRYPTOGRAPHIC-MATERIAL-FILE**, der der im vorigen Beispiel erstellen `cryptographicMaterialContainer.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.

**Anmerkung**: Sollten sich mehrere Belege in der Datei **QR-CODE-REP-FILE** befinden, so wird deren Verkettung **NICHT** überprüft. Diese Prüfung wird nur bei der DEP-Export-Format Prüfung durchgeführt.
                    
##Testfälle
Die Tesfälle sind im Mustercode der Demokasse integriert bzw. können durch Download und Entpacken von `regkassen-test-cases-0.6.zip` (siehe [https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases](https://github.com/a-sit-plus/at-registrierkassen-mustercode/releases)) bezogen werden.
Eine detallierte Beschreibung der Testfälle befindet sich im Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)*. Diese Beschreibung umfasst:

 - Die Beschreibung der verwendeten Datenformate der Testfälle für eine automatisierte Verarbeitung in einem Kassensystem, um die definierten Testbelege und Belegabläufe erstellen zu können.
 - Erklärungen zu den unterschiedlichen Testfällen und deren Hintergründe.

 
##BUILD Prozess
Um den Maven Build-Prozess eigenständig durchzuführen, sind in den jeweiligen Verzeichnissen folgende Schritte notwendig:

      regkassen-core: mvn install
      regkassen-democashbox: mvn install
      
In den Verzeichnissen `regkassen-democashbox`, `regkassen-verification` befinden sich nach dem erfolgreichen Build-Prozess die JAR Dateien (im Unterverzeichnis "target"), die zum Ausführen benötigt werden (siehe Punkte zur Verwendung des Demo-Codes weiter oben).

#Erläuterungen zur Detailspezifikation der Verordnung/FAQs
Etwaige Erläuterungen werden im Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)* ergänzt. Bis sie dort einfließen können, werden Sie in diesem WIKI aufbewahrt:
[https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erläuterungen-FAQ](https://github.com/a-sit-plus/at-registrierkassen-mustercode/wiki/Erl%C3%A4uterungen-FAQ)

# Impressum
Informationen zu A-SIT und A-SIT Plus unter http://www.a-sit.at

A-SIT Plus GmbH
A-1030 Wien,
Seidlgasse 22 / 9
1030 Wien
FN 436920 f,
Handelsgericht Wien

