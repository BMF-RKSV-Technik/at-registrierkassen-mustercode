# Wichtige Hinweise
**22.06.2020 Senkung des Umsatzsteuersatzes auf 5% und Auswirkungen auf Registrierkassen**:

Auf der Homepage des BMF wurden FAQ's für die Umsetzung des 5%-igen Umsatzsteuersatzes veröffentlicht:
- https://www.bmf.gv.at/public/informationen/informationen-coronavirus/registrierkassen.html

**04.01.2018 Release der Version 1.1.1 des Prüfmoduls**:
Es wurde ein Problem beim Speicherverbrauch behoben (Vielen Dank für den Hinweis an ztp-mino!). Der Speicherverbrauch von V 1.1.0 erhöhte sich pro geprüftem Beleg. Die Ursache dafür war ein Fehler bei der Verarbeitung der Prüfergebnisse: Diese wurden zu lange im Speicher gehalten. 
Bitte beachten: Trotzdem ist ein konstanter Speicherverbrauch - unabhängig von der Anzahl der Belege - nicht möglich, da bestimmte Informationen im Speicher behalten werden müssen (z.B. jede Belegnummer, um die Prüfung auf die Mehrfachverwendung durchzuführen).

**22.12.2017 Release der Version 1.1.0 des Prüfmoduls**:
Es wird die Version 1.1.0 des Prüftools freigegeben. Detailinformationen zu den Änderungen und Ergänzungen: Siehe "Change-Log" im Abschnitt "Prüftool 1.0.0 bis 1.1.0"

**12.12.2016 Ergänzung der Infos zu Fehlersuche bei ungültigen Signaturen**:

Die vorliegende Präsentation wurde um Informationen zu Padding-Zeichen im JWS-Standard ergänzt. Die neue Version des Dokuments kann von [hier](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/blob/master/Dokumente/2016-12-12%20SIG-Check.pdf) bezogen werden.


**18.11.2016 Fehlersuche bei ungültigen Signaturen**:

Es stehen nun Folien für die Fehlersuche bei kryptographisch ungültigen Signaturen zur Verfügung (dieses Thema wurde auch am 27.10.2016 im Vortrag bei der WKO angesprochen). Die Datei liegt im Repository bzw. kann direkt von [hier](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/raw/master/Dokumente/2016-11-18%20SIG-Check.pdf) bezogen werden.
 
**18.11.2016 Signieren von Nullbelegen**:

Die Version 1.0.0 des Prüftools gibt eine Fehlermeldung aus wenn ein Nullbeleg nicht signiert ist. Dazu gibt es folgende Erläuterungen (abgestimmt mit dem BMF):

 - **Nullbelege, die eine gültige Signatur enthalten** müssen (also nicht mit “Sicherheitseinrichtung ausgefallen” markiert werden dürfen): 
	 - Startbeleg
	 - Jahresbeleg
	 - Sammelbeleg, der das Ende des Ausfalls der Sicherheitseinrichtung markiert
 - **Nullbelege die auch mit “Sicherheitseinrichtung ausgefallen”** markiert werden dürfen: 
	 - Monatsbeleg
	 - Schlussbeleg
	 - Kontrollbelege, die über Aufforderung der Behörde erstellt werden müssen
	 - Freiwillige Nullbelege: z.B. Tagesbelege etc.
	 - "Zufällige Nullbelege” die z.B. aufgrund einer Gutschrift entstehen

Im Prüftool mit der Version 1.0.0. wird bei allen Nullbelegen verlangt, dass eine Signatur vorhanden ist (also der Beleg nicht mit “Sicherheitseinrichtung ausgefallen” markiert ist). Diese Funktion wird in der nächsten Version wie folgt geändert: Das Prüfergebnis wird nach wie vor bei allen Nullbelegen ohne Signatur negativ sein, an Stelle der Fehlermeldung wird allerdings ein Hinweis ausgegeben, der auf die oben genannte Kategorisierung verweist. Der Prüfvorgang wird nicht abgebrochen. Da es sich um den selben Prüfkern wie im FinOnline handelt, wird auch dort die gleiche Meldung zu sehen sein.

# Übersicht
Dieses Projekt dient der Behandlung technischer Sachverhalte der RKSV. Wir bitten daher um Verständnis, dass rechtliche/organisatorische Themen im Allgemeinen nicht beantwortet werden können. Für die Beantwortung solcher Fragen bitten wir Sie, die Informationen des BMFs heranzuziehen (siehe https://www.bmf.gv.at/steuern/selbststaendige-unternehmer/registrierkassen_startseite.html). Sollte Ihr Anliegen dort nicht behandelt sein, bitten wir Sie, die am Ende dieses Dokuments genannten Kontaktpunkte bzw. Informationsquellen zu berücksichtigen.
 
Ein weiterer Hinweis bezüglich den “Issues” in diesem Projekt: Offizielle Aussagen des BMF werden nur von den Benutzern der Organisation BMF-RKSV-Technik (**Grigo-S, WienerroitherM**) getätigt. Für die Korrektheit der Aussagen anderer Benutzerinnen und Benutzer kann keine Garantie übernommen werden.

Dieses Dokument ist wie folgt organisiert:
 
 - **Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)**: In diesem Abschnitt werden Informationen zum gleichnamigen Dokument gegeben. Dieses Dokument beschreibt RKSV-relevante Prozesse im Detail und stellt neben dem Muster-Code die technischen Informationen für die Umsetzung in einer Kasse zur Verfügung.
 - **Prüftool**: Das Prüftool ermöglicht es den Kassenherstellern, vorab die erstellten maschinenlesbaren Codes und RKSV-DEP-Export-Dateien zu überprüfen.
 - **Muster-Code**: Dieser Abschnitt beinhaltet relevante Informationen zum Muster-Code, der die RKSV-relevanten Elemente einer Kasse demonstriert. Der Muster-Code setzt dabei die Prozesse des Dokuments "Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)" um.
 - **Testfälle**: Dieser Abschnitt stellt Testfälle für die Überprüfung der Implementierung einer Kasse zur Verfügung.
 - **Kontakt/Fragen**: Dieser Abschnitt beinhaltet Kontaktinformationen für Detailfragen, die nicht im Rahmen dieses Projekts beantwortet werden können.

# Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)
In Zusammenarbeit zwischen dem BMF und A-SIT Plus wurde das Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)* erstellt. Es enthält Festlegungen in technischen Detailfragen zur RKSV auf Prozessebene und Klarstellungen bzw. Ergänzungen im Bereich der Muster-Code-Beispiele. 

**Releases**:

 - **Version 1.2 (06.09.2016)**: [Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV) V1.2](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/download/1.2-DOK/2016-09-05-Detailfragen-RKSV-V1.2.pdf): 
	 - **Liste der Änderungen**: Es wurden die bei V1.1. unter "Bekannte Probleme" angemerkten Fehler behoben und der Algorithmus zur Berechnung der Prüfsumme des AES-Schlüssels für die manuelle Übermittlung im FinanzOnline hinzugefügt.
		 - siehe Change-Log im Dokument
		 - [Diff (Änderungsmarkierungen) von V1.1 zu V1.2](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/download/1.2-DOK/2016-09-05-Diff-Detailfragen-RKSV-V1.1-V1.2.pdf)

 - **Version 1.1 (11.03.2016)**: [Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV) V1.1] (https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/download/1.1-DOK/2016-03-11-Detailfragen-RKSV-V1.1.pdf): 
	 - **Liste der Änderungen**: Im Wesentlichen wurden die Kapitel um die ersten Kassen-Testfälle ergänzt, sowie diverse Fehlerbehebungen vorgenommen. Details zu den Änderungen:
		 - siehe Change-Log im Dokument
		 - [Diff (Änderungsmarkierungen) von V1.0 zu V1.1](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/download/1.1-DOK/2016-03-11-Diff-Detailfragen-RKSV-V1.0-V1.1.pdf)
	 - **Bekannte Probleme**:
		 - https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/issues/48
		 - https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/issues/52
		 - https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/issues/54
		 - https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/issues/56
		 - https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/issues/77
 - **Version 1.0 (19.02.2016)**:
	 - 	[Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV) V1.0](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/files/137544/2016-02-18-Detailfragen-RKSV-V1.0.pdf)

# Prüftool 1.0.0 bis 1.1.0
Das gegenständliche, im Auftrag des BMF erstellte, Prüftool ab Version 1.0.0 überprüft Format und Inhalt sowie Abfolge von Einzelbelegen in einem exportierten RKSV-Datenerfassungsprotokoll (RKSV-DEP) einer Registrierkasse auf Übereinstimmung mit den ab 1.4.2017 gültigen Vorgaben der RKSV. Die Prüfalgorithmen, die dabei zur Anwendung kommen, stimmen im Wesentlichen mit jenen überein, die auch für Einzelbelegüberprüfungen und RKSV-DEP-Überprüfungen des BMF herangezogen werden (z.B.: BMF Belegcheck-App). Dadurch, dass das gegenständliche Prüftool primär auf die Überprüfung der korrekten Implementierung ausgerichtet ist, kann es dazu kommen, dass das Prüftool etwas als fehlerhaft ausgibt, das bei der Behördenprüfung als fehlerfrei angesehen wird (z.B. nicht vorhandener Startbeleg bei der Überprüfung eines DEP-Ausschnittes).

Um die Prüfung mit dem Prüftool durchzuführen, müssen dem Prüftool auch der verwendete AES-Schlüssel sowie die verwendeten Zertifikate/öffentliche Schlüssel in einer Datei als Parameter übergeben werden (siehe Details weiter unten).

Die mitgelieferten Testfälle sollten im Zuge der Kassenimplementierung verwendet werden, auch um die Korrektheit des Verhaltens der Kasse bei unterschiedlichen Varianten der Belegabfolgen überprüfen zu können. Es wird aber darauf hingewiesen, dass mit den mitgelieferten Testfällen nur die allgemein gültigen Fallkonstellationen abgedeckt werden. Für eine vollständige Abdeckung der Fallkonstellationen im Zusammenhang mit der spezifischen Kassenlösung werden weitere Testfälle erforderlich sein, die vom Kassenhersteller beizusteuern sind.

Im Zusammenhang mit dem Prüftool und den durchgeführten Prüfungen müssen folgende Quellen berücksichtigt werden:

 - [Erlass zur Einzelaufzeichnungs-, Registrierkassen- und Belegerteilungspflicht](https://findok.bmf.gv.at/findok?execution=e1s1)
 - [Registrierkassensicherheitsverordnung und Detailspezifikationen](https://www.ris.bka.gv.at/GeltendeFassung.wxe?Abfrage=Bundesnormen&Gesetzesnummer=20009390&FassungVom=2017-04-01)
 - [Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)](https://www.bmf.gv.at/egovernment/projekte/registrierkassen/registrierkassen-beispiele-detailspezifikation.html)

***Change-Log*** 

 - **22.12.2017**: Release 1.1.0 veröffentlicht. Dabei wurden folgende Änderungen durchgeführt:
      - Speicherverbrauch stark reduziert (im Wesentlich unabhängig von der Anzahl der Belege, konstanter Verbrauch aber nicht möglich da bestimmte Informationen global gespeichert werden müssen: z.B. für Prüfung ob Belegnummer eindeutig ist)
      - Hinzufügen von Prüfungen
	    - globale RKSV-DEP Prüfungen: Basiseigenschaften des RKSV-DEP-Exports, Korrektheit des JSON-Formats
	    - RKSV-DEP Prüfungen pro Beleg: Chronologische Reihenfolge der Belege
      - Änderung von bestehenden Prüfungen
	    - Fehler bei einzelnen Belegen führen nicht zum Abbruch des Prüftools. Dies gilt vor allem für das Behandeln von nicht-signierten Nullbelegen.
      - Änderungen/Ergänzungen der Output-Dateien: siehe Detailbeschreibung im Abschnitt "Verwendung des Prüftools".
 - **21.10.2016**: Release 1.0.0 veröffentlicht. Im Vergleich zu Version 0.7.1 wurde eine detaillierte Qualitätssicherung der RKSV-DEP-Überprüfungen durchgeführt (die Einzelbelegprüfung hatte auch schon in Version 0.7.1 diesen Status). Im Zuge der Qualitätssicherung wurden noch weitere Detailprüfungen hinzugefügt, die es ermöglichen, einfache Implementierungsfehler zu erkennen (Belege mit gleicher Nummer im RKSV-DEP,  mehrere Kassen-IDs im RKSV-DEP, Mischung mehrerer Systemtypen). Die Erweiterungen werden im Detail genannt:

	 - **Sammelbeleg nach ausgefallener Sicherheitseinrichtung**: Es wird nun akzeptiert, dass ein signierter Nullbeleg auch an zweiter Stelle nach einem Beleg mit einer ausgefallenenen Sicherheitseinrichtung im RKSV-DEP folgen darf. In Version 0.7.1 wurde es nur als richtig anerkannt, wenn der signierte Nullbeleg unmittelbar nach dem Beleg mit ausgefallener Sicherheitseinrichtung folgte (für Details siehe *"Erlass zur Einzelaufzeichnungs-, Registrierkassen- und Belegerteilungspflicht",* Abschnitt 3.6.1.).
	 - **Überprüfung, ob nur eine Kassenidentifikationsnummer im RKSV-DEP-Export enthalten ist**: Es wird überprüft, ob pro RKSV-DEP-Export nur eine Kassenidentifikationsnummer enhalten ist. Laut RKSV darf in einem exportierten RKSV-DEP nur eine Kasse abgebildet werden.
	 - **Überprüfung, ob mehrere Belege mit der gleichen Belegnummer im RKSV-DEP Export enthalten sind**: Es wird überprüft, ob pro RKSV-DEP-Export jede Belegnummer nur einmal zur Verwendung kommt.
	 - **Belege mit zukünftigem Datum**: Es wurde der Paramater **-f** eingeführt, der das Prüftool instruiert, auch zukünftige Belege als gültig anzuerkennen. Dies gilt nur für das Prüftool, erleichtert aber das Überprüfen von spezifischen Testfällen.
	 - **RKSV-DEP-Prüfungsdetails**: Die Prüfergebnisse werden wie in Version 0.7.1 in der Datei DEP.json abgelegt. Zusätzlich werden aber - wie für die Einzelbelegprüfung - pro Beleg die RKSV-DEP-Prüfergebnisse für den vorliegenden Beleg in einer Datei gespeichert (z.B. 0001_dep.json für den 2. Beleg im RKSV-DEP). Details dazu werden weiter unten genannt.
	 - **Indizierung der Prüfergebnisse**: Indices für Belege im RKSV-DEP starten ab Version 1.0.0. mit dem Index 0. In Version 0.7.1 war der Startindex gleich 1.
 - **07.09.2016**: Release 0.7.1 veröffentlicht
	 - Bugfixes:
		 - Minimallänge des Belegs auf 100 statt 150 Zeichen geändert.
 -  **05.09.2016**: Release 0.7 veröffentlicht: Das gesamte Prüftool wurde geändert, der Prüfkern liefert nun die gleichen Ergebnisse wie sie auch im FinanzOnline oder über das WebService zur Verfügung stehen.

***Verwendung des Prüftools***

Download und entpacken von `regkassen-verification-1.1.0.zip` (siehe [https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases)).

***DEP-Export Format***

Mit dieser Variante kann der gesamte RKSV-DEP-Export überprüft werden.

    java -jar regkassen-verification-depformat-1.1.0.jar -v -f -i DEP-EXPORT-FILE -c CRYPTOGRAPHIC-MATERIAL-FILE -o OUTPUT_DIR
	         
Wobei

 - der Parameter **DEP-EXPORT-FILE** der im Muster-Code erstellten `dep-export.json` Datei entspricht (RKSV-DEP Exportformat laut RKSV). Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - der Parameter **CRYPTOGRAPHIC-MATERIAL-FILE** den Pfad zur Datei angibt, die die notwendigen Daten (öffentliche Schlüssel, Zertifikate und AES-Schlüssel) für die Prüfung des exportieren RKSV-DEP enthält. In dem BSP-Outputs des Muster-Codes werden diese Daten in der Datei `cryptographicMaterialContainer.json` abgelegt.  Details zum Format können im weiter oben referenzierten Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)* abgerufen werden. Diese Datei ist nicht im Sinne der RKSV gefordert und **ist nur für die Verwendung des Prüftools relevant**, da dieses die Informationen bezüglich der verwendeten Zertifikate und AES-Schlüssel nicht aus FinanzOnline abrufen kann.
 - der Parameter **-o OUTPUT_DIR** ein Verzeichnis angibt, in dem die Detailergebnisse des Prüftools gespeichert werden.
 - der optionale Parameter **-f** angibt, dass Belege mit zukünftigem Belegerstellungsdatum als gültig anerkannt werden.
 - der optionale Parameter **-v** angibt, dass detaillierte Informationen auf der Konsole ausgegeben werden. Die Detailstufen der Dateien im Verzeichnis **OUTPUT_DIR** werden davon nicht beeinflusst. 

***Weitere Details zum Prüfablauf:***

 - **RKSV-DEP-Export-Prüfungen pro Beleg**: Es werden folgende Prüfungen durchgeführt:
	 - Gültige Verkettung der maschinenlesbaren Codes zwischen den Belegen im exportierten RKSV-DEP
	 - Korrekte Entwicklung des Umsatzzählers
	 - Richtige Abfolge der Belegtypen im RKSV-DEP (z.B.: Startbeleg nur an erster Stelle, Sammelbeleg nach ausgefallener Sicherheitseinrichtung)
	 - Korrekte chronologische Abfolge der Belege
	 - Diverse Prüfungen: Nur eine Kassen-ID pro RKSV-DEP, jede Belegnummer darf nur einmal im RKSV-DEP vorkommen, keine Vermischung von Systemtypen (geschlossen, offen)
 - **Globale RKSV-DEP Prüfungen**:
     -  Basisanforderungen an das RKSV-DEP-Format
     - Korrektheit des JSON-Formats des RKSV-DEP-Export
 - **Einzelne maschinenlesbare Codes**: Jeder maschinenlesbare Code im RKSV-DEP-Export wird im Detail geprüft: Es werden dabei die gleichen Prüfungen wie im FinanzOnline durchgeführt und das Ergebnis im gleichen Format aufbereitet. Das Prüftool hat keinen Zugriff auf FinanzOnline, daher wird für den Registrierungstatus der Kasse und der verwendeten Siegel- bzw. Signaturerstellungseinheit immer der korrekte Wert angenommen. Die Detailergebnisse werden im angegeben Verzeichnis (**OUTPUT_DIR**) abgelegt. Dieses Verzeichnis enthält dabei für jeden maschinenlesbaren Code die Detailergebnisse, wobei **N** der Nummerierung der maschinenlesbaren Codes im RKSV-DEP-Export entspricht (beginnend mit 0). Kommt es zu einem Fehler bei der Einzelprüfung, werden die restlichen maschinenlesbaren Codes weiter geprüft, die RKSV-DEP-Prüfungen werden aber an dieser Stelle abgebrochen.

***Prüfergebnisse:***
 - **N_cashbox.json**: Dieses Ergebnis enspricht dem Ergebnis, das in FinanzOnline visuell aufbereitet wird und als Datei zu beziehen ist. Ebenso wird das Prüfergebnis im gleichen Format vom Web-Service für die Kassen zur Verfügung gestellt. Es werden nur die fehlerhaften Prüfergebnisse angezeigt. (**N** entspricht der Belegnummer im RKSV-DEP-Export, beginnend mit 0. Sind mehrere Beleggruppen im RKSV-DEP-Export vorhanden, wird die Nummerierung der maschinenlesbaren Codes nicht zurückgesetzt.)
 - **N_cashbox_full.json**: Es werden auch die positiven Prüfergebnisse angezeigt. Der Hersteller hat hier die Möglichkeit, das vollständige Prüfergebnis einzusehen und alle durchgeführten Prüfungen zu erkennen. (**N** entspricht der Belegnummer im RKSV-DEP-Export, beginnend mit 0. Sind mehrere Beleggruppen im RKSV-DEP-Export vorhanden, wird die Nummerierung der maschinenlesbaren Codes nicht zurückgesetzt.)
 - **N_app.json**: Dieses ist äquivalent zu dem der Belegcheck App. Für Detailanalysen hat es keine direkte Relevanz, es zeigt aber, wie etwaige Fehler in der App repräsentiert werden. (**N** entspricht der Belegnummer im RKSV-DEP-Export, beginnend mit 0. Sind mehrere Beleggruppen im RKSV-DEP-Export vorhanden, wird die Nummerierung der maschinenlesbaren Codes nicht zurückgesetzt.)
 - **N_dep.json**: Diese Datei enthält die detaillierten Prüfergebnisse der RKSV-DEP-Prüfungen pro Beleg zusammen (siehe oben, Punkt "RKSV-DEP-Export-Prüfungen pro Beleg") . (**N** entspricht der Belegnummer im RKSV-DEP-Export, beginnend mit 0. Sind mehrere Beleggruppen im RKSV-DEP-Export vorhanden, wird die Nummerierung der maschinenlesbaren Codes nicht zurückgesetzt.) Diese Dateien werden seit V 1.0.0 erstellt.
 - **DEP-global.json** (seit Version 1.1.0): Diese Datei fasst die Ergebnisse der globalen RKSV-DEP Prüfungen zusammen (siehe oben, Punkt "Globale RKSV-DEP Prüfungen"): Basiseigenschaften des RKSV-DEP, Korrektheit des JSON-Formats sowie die Zusammenfassung der Ergebnisse aller Prüfungen die pro Beleg durchgeführt werden.
 - **DEP-full.json** (seit Version 1.1.0): Diese Datei fasst enthält die detaillierte Auflistung aller Prüfungen die pro Beleg durchgeführt werden. Es handelt sich dabei um alle Einzelbelegprüfungen (siehe **N_cashbox_full.json**) und RKSV-DEP-Prüfungen die pro Beleg durchgeführt werden (siehe **N_DEP.json**).
 - 	**DEP.json** (bis Version 1.0.0): In dieser Datei wurden die Detailergebnisse der RKSV-DEP-Export-Prüfung (Verkettung, Entwicklung Umsatzzähler, Abfolge der maschinenlesbaren Codes) ausgegeben. Diese Datei war bis V1.0.0 enthalten. Die Inhalte der Datei wurden durch die Einzelergebnisse der Dateien **N_dep.json** ersetzt. Mit Version 1.1.0 wurde diese Datei durch die Dateien **N_dep.json**, **DEP-global.json** sowie **DEP-full.json** ersetzt.

***QR-Code-Repräsentation eines einzelnen oder mehrerer maschinenlesbaren Codes***

In dieser Variante werden einzelne maschinenlesbare Codes auf Ihre Gültigkeit überprüft. Die Prüfverfahren entsprechen jenen, die auch beim RKSV-DEP-Export-Prüftool für die Einzelbelegprüfung zum Einsatz kommen. **ACHTUNG**: Es werden keine RKSV-DEP-relevanten Prüfungen durchgeführt (Verkettung, Abfolge, etc.). Die Prüfung der einzelnen Belege erfolgt unabhängig voneinander.

    java -jar regkassen-verification-receipts-1.1.0.jar -v -f -i QR-CODE-REP-FILE -c CRYPTOGRAPHIC-MATERIAL-FILE -o OUTPUT_DIR

Wobei

 - der Parameter **QR-CODE-REP-FILE** der vom Muster-Code erstellen `qr-code-rep.json` Datei entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - der Parameter **CRYPTOGRAPHIC-MATERIAL-FILE** der im vorigen Beispiel erstellen Datei `cryptographicMaterialContainer.json` entspricht. Für den schnellen Test kann die entsprechende Datei aus dem Beispiel-Output übernommen werden.
 - der Parameter **-o OUTPUT_DIR** ein Verzeichnis angibt, in dem die Detailergebnisse des Prüftools gespeichert werden.
 - die Parameter **-v** und **-f** die gleiche Funktionsweise wie beim RKSV-DEP-Export-Prüftool haben.

Die Ergebnisse ensprechen dem Format, das unter der Prüfung "Einzelne maschinenlesbare Codes" für den RKSV-DEP-Export beschrieben ist.


# Muster-Code

Dieses Projekt stellt Demo-Code als Begleitung zur [Registrierkassensicherheitsverordnung (RKSV)](https://www.ris.bka.gv.at/GeltendeFassung.wxe?Abfrage=Bundesnormen&Gesetzesnummer=20009390&FassungVom=2017-04-01) zur Verfügung und wurde in der Zusammenarbeit zwischen BMF und A-SIT Plus erstellt. Der Demo-Code zeigt

* wie die wesentlichen Elemente der Detailspezifikation der Verordnung in Software implementiert werden können und
* gibt zusätzliche Erläuterungen zu Aspekten der Detailspezifikation, die noch Interpretationsspielraum zulassen.

In diesem Projekt werden nur technische Aspekte der Registrierkassensicherheitsverordnung betrachtet. Die Informationen und der Code werden laufend erweitert und mit typischen Fragen/Antworten ergänzt.

Diese Projektseite verwendet Deutsch als Sprache. In den textuellen Ergänzungen im Source Code wird Englisch verwendet.

Der Muster-Code wird unter der Apache 2.0 Lizenz (http://www.apache.org/licenses/LICENSE-2.0) zur Verfügung gestellt. Der Code für die Prüftools wird nicht veröffentlicht. Die Verwendung der Prüftools ist natürlich frei möglich.

Alle verwendeten Dritt-Bibliotheken und deren Lizenzen sind in den Maven Build Dateien (pom.xml) der einzelnen Module ersichtlich und auf der folgenden WIKI-Seite zusammengefasst:

https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/wiki/Lizenzen-Dritt-Bibiliotheken

***Change-Log***

 - **19.10.2016**: Release 1.0.0 veröffentlicht: Es gibt keine relevanten Änderungen gegenüber Version 0.7.1. Die Versionierung wurde aufgrund der Kompatibilität mit dem Prüftool angepasst.
 - **07.09.2016**: Release 0.7.1 veröffentlicht
	 - Bugfixes:
		 - In mehreren Test-Szenarien gab es Belege ohne Belegtyp. Dies wurde nun korrigiert.
 - **05.09.2016**: Release 0.7 veröffentlicht
	 - **Änderungen**:
		 - **Testfälle**: Nullbelege müssen eine gültige Signatur haben. Dies wurde in den Testfällen für den Beispiel-Code entsprechend korrigiert.
		 - **Umsatzzähler mit Länge ungleich 8**: Der Muster-Code zeigt nun die Aufbereitung eines Umsatzzählers ungleich der Länge 8. Mit dem Parameter "l" kann die Länge des Umsatzzählers für die Erstellung der Beispielbelege definiert werden.
		 - **Organisation des Codes**: Diverse Pakete wurden umbenannt bzw. in andere Teile des Codes verschoben.
	 - **Bugfixes**:
		 - **Datum aus Testfällen**: Bisher wurde das Datum aus den Test-Szenarien für die Erstellung der maschinenlesbaren Codes nicht übernommen. Dies wurde nun korrigiert.
		 - **Falsche Aufsummierung des Umsatzzählers**: In sehr seltenen Fällen kam es aufgrund eines Rundungsfehlers zur falschen Aufsummierung des Umsatzzählers. Dieses Problem wurde behoben.
		 - **Kein Belegtyp in Test-Szenario 1**: Im Test-Szenario 1 hatte der Beleg mit der ID 66 keinen Belegtyp. Dies wurde nun korrigiert.
		
***Verwendung des Democodes und der Demokasse***

Neben dem Source Code wird auch immer eine ZIP Datei der ausführbaren Dateien zur Verfügung gestellt. Die neueste Version ist immer unter [Releases](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases) zu finden. Für das Ausführen der Demokasse sind folgende Voraussetzungen nötig:

* *Java VM*: Es wird eine aktuelle Java VM (JRE ausreichend) mit Version >= 1.7 benötigt.
* *Kryptographie*: Der Registrierkassen-Demo-Code verwendet starke Kryptographie (z.B. AES mit 256 bit Schlüssel), der mit den Standard-Export Policies der Java VM nicht ausgeführt werden kann. Es muss daher die "Unlimited Strength Policy" von Oracle installiert werden. Siehe: [http://www.oracle.com/technetwork/java/javase/downloads/index.html](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

Um die Demokasse zu verwenden, wird wie folgt vorgegangen: 
Download und entpacken von `regkassen-demo-1.0.0.zip` (siehe https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases).

Ausführen der Demokasse für die Abarbeitung der integrierten Testfälle mit
`java -jar regkassen-demo-1.0.0.jar -o OUTPUT_DIR -v -c -l 8`

**Parameter**: 

 - Der optionale Parameter **-o OUTPUT_DIR** gibt ein Verzeichnis an, in dem die vom Demo-Code erstellten Daten/maschinenlesbaren Codes gespeichert werden. Wenn die Option **-o** nicht angegeben wird, werden die Ergebnisse in ein automatisch generiertes Verzeichnis im Arbeitsverzeichnis gespeichert. Format für das erstellte Verzeichnis: `CashBoxDemoOutputyyyy-MM-dd'T'HH-mm-ss` wobei `yyyy-MM-dd'T'HH-mm-ss` der aktuellen Zeit im angegebenen Format entspricht.
 - **-v (verbose)**: Der optionale Parameter **-v** gibt an, ob die generierten Daten (maschinenlesbare Codes, RKSV-DEP-Export etc.) auch über STDOUT ausgegeben werden. Ist **-v** nicht angegeben, so werden die Daten nur in das Output-Verzeichnis geschrieben.
 - **-c (closed-system)**: Der optionale Parameter **-c** gibt an, ob es sich um ein geschlossenes System handelt. In diesem Fall werden statt der Seriennummer des Zertifikats der Ordnungsbegriff des Unternehmens und das Identifikationsmerkmal des verwendeten Schlüssels in den erstellten maschinenlesbaren Codes verwendet. Außerdem werden statt den X509-Zertifikaten öffentliche Schlüssel für die Signaturprüfung zur Verfügung gestellt (siehe Datei `cryptographicMaterialContainer.json`).
 - Der optionale Parameter **-l TURNOVER-COUNTER-LENGTH** gibt an, wieviele Bytes für die Kodierung des Umsatzzählers verwendet werden sollen. Wird der Parameter nicht angegeben, oder wird ein Wert kleiner 5 oder größer 8 angegeben, so werden 8 Bytes für die Kodierung des Umsatzzählers verwendet.
 
**Das Output-Verzeichnis enthält folgende Dateien/Verzeichnisse**:

Für jeden Test-Fall wird ein eigenes Verzeichnis angelegt, das den Namen des Testfalls erhält.  In diesem Verzeichnis werden unterschiedliche Dateien/Verzeichnisse gespeichert. Die folgenden Dateien bzw. deren Formate haben zwar für eine produktive Kasse (mit Ausnahme des RKSV-DEP-Exports) keine Bedeutung, allerdings spielen sie bei der Überprüfung der Implementierung der Kasse eine wichtige Rolle, da die Dateien vom Prüftool verwendet werden, um die Testfälle einer Kasse und vor allem deren Abdeckung prüfen zu können:

 - **dep-export.json (Datei)**: In dieser Datei werden die erstellten maschinenlesbaren Codes im RKSV-DEP-Export-Format (Detailspezifikation, Abs. 3) gespeichert. Weitere Details dazu: Siehe Abschnitt 7 im Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)*.
 - **cryptographicMaterialContainer.json (Datei)**: Diese Datei enthält den AES-Schlüssel sowie die Zertifikate bzw. öffentlichen Schlüssel, die für die Prüfung der maschinenlesbaren Codes nötig sind. Weitere Details dazu: Siehe Abschnitt 7 im Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)*.

Die folgenden Dateien dienen nur zur Demonstrationszwecken und haben für das Prüftool keine Relevanz:

 -  **ocr-code-rep.json (Datei)**: Die textuelle Repräsentation der maschinenlesbaren OCR-Codes als JSON-Array. Eine Zeile der Datei entspricht der OCR-Code Repräsentation eines maschinenlesbaren Codes.
 - **qr-code-dir-pdf (Verzeichnis)**: Einfache Demo-PDF-Belege, die mit dem QR-Code bedruckt wurden.
 - **ocr-code-dir-pdf (Verzeichnis)**: Einfache Demo-PDF-Belege, die mit dem OCR-Code bedruckt wurden.

Ein Beispiel für den Output ist auch direkt ohne Ausführen des Demo-Codes verfügbar: `example-output-1.0.0.zip` (siehe [https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases)).

***BUILD Prozess***

Um den Maven Build-Prozess eigenständig durchzuführen, sind in den jeweiligen Verzeichnissen folgende Schritte notwendig:
      regkassen-common: mvn install
      regkassen-core: mvn install
      regkassen-democashbox: mvn install
      
In den Verzeichnissen `regkassen-democashbox`, `regkassen-verification` befinden sich nach dem erfolgreichen Build-Prozess die JAR Dateien (im Unterverzeichnis "target"), die zum Ausführen benötigt werden (siehe Punkte zur Verwendung des Demo-Codes weiter oben).
                    
##Testfälle

Die Tesfälle sind im Mustercode der Demokasse integriert bzw. können durch Download und Entpacken von `regkassen-test-cases-1.0.0.zip` (siehe [https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases](https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases)) bezogen werden.
Eine detallierte Beschreibung der Testfälle befindet sich im Dokument *Festlegungen des BMF zu Detailfragen der Registrierkassensicherheitsverordnung (RKSV)*. Diese Beschreibung umfasst:

 - Beschreibung der verwendeten Datenformate der Testfälle für eine automatisierte Verarbeitung in einem Kassensystem, um die definierten maschinenlesbaren Codes und deren Abfolge erstellen zu können.
 - Erklärungen zu den unterschiedlichen Testfällen und deren Hintergründe.

 
#Kontakt/Fragen

Es wurde dazu eine Projektseite von der WKO eingerichtet. Es ist dazu eine Registrierung bei der WKO notwendig.

[Projektseite der WKO](https://communities.wko.at/Kassensoftware/default.aspx)

Etwaige Fragen sollten dort im Forum gestellt werden, um eine möglichst effiziente Beantwortung durchführen zu können. Es stehen dort die Rubriken "FinanzOnline – Webservice und File Upload" und "BMF Belegcheck-App (Belegprüfung) erweitert“ für Fragen zur Verfügung.

[Forum der WKO](https://communities.wko.at/Kassensoftware/Lists/Forum/)

