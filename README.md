# Change Log
* **02.10.2015**: Release 0.1 auf GitHub veröffentlicht

# Überblick
Dieses Projekt stellt Demo-Code als Begleitung zur Registrierkassensicherheitsverordnung (RKSV) (https://www.bmf.gv.at/steuern/Registrierkassensicherheitsverordnung.html) zur Verfügung. Der Demo-Code zeigt

* wie die wesentlichen Elemente der Detailspezifikation der Verordnung in Software implementiert werden können,
* gibt zusätzliche Erläuterungen zu Aspekten der Detailspezifikation die noch Interpretationsspielraum zulassen,
* und stellt Prüfwerkzeuge zur Verfügung, die es erleichtern die korrekte Umsetzung einer Registrierkasse im Sinne der Detailspezifikation zu überprüfen (in der initalien Version vom 02.10.2015 sind diese Prüfwerkzeuge auf Basisfunktionen beschränkt).

In diesem Projekt werden vorwiegend technische Aspekte der Verordnung betrachtet. Die Informationen und der Code werden laufend erweitert und mit typischen Fragen/Antworten ergänzt.

## Wichtige Anmerkungen/Einschränkungen
* *Version 0.1 (02.10.2015)*: Mit Version 0.1 werden im Demo-Code die wesentlichen Elemente in Bezug auf Signaturaufbereitung, Signaturerstellung, Erstellung des QR-Codes, Erstellung des OCR-Codes sowie der Export des Daten-Erfassung-Protokolls (DEP) demonstriert. Erweiterte Themen wie z.B. die korrekte Behandlung des Ausfalls der Signatureinheit, werden in späteren Versionen behandelt.
* *Wichtige Anmerkung*: Die Version 0.1 demonstriert wie mit den unterschiedlichen Elementen (Signature, QR-Code etc.) umgegangen werden muss. Obwohl hier nach bestem Gewissen vorgegangen wurde, kann keine GARANTIE für die korrekte Funktionsweise übernommen werden. Um hier in den nächsten Versionen mehr Klarheit bieten zu können, werden vom aktuellen Code unabhängige Prüfwerkzeuge geschaffen. Diese ermöglichen den Kassaherstellern die jeweiligen Produkte so weit wie möglich auf das korrekte Verhalten mit Relevanz für die Verordnung zu überprüfen.
* *Sprache*: Diese Projektseite verwendet Deutsch als Sprache. In den textuellen Ergänzungen im Source Code wird Englisch verwendet.

##Weiteres Vorgehen
Diese Plattform wird für die weitere Bereitstellung von Demo-Code, für das Bereitstellen von Prüfwerkzeugen und für die Erstellung von FAQs verwendet. Die weiteren Versionen des Codes werden demnächst veröffentlicht und fokussieren sich auf erweiterte Verwendungsmuster (z.B. Ausfall Sicherheitseinrichtung) und das Bereitstellen von Prüfwerkzeugen die es ermöglichen erstellte Belege, DEP-Export-Dateien auf Ihre Korrektheit zu prüfen. Auch wird dieses Projekt laufend um Antworten zu häufig gestellten Fragen ergänzt.

##Kontakt/Fragen
Informationen werden hier veröffentlicht.

## Lizenzen
Der gesamte Code wird unter der Apache 2.0 Lizenz zur Verfügung gestellt (http://www.apache.org/licenses/LICENSE-2.0). Alle verwendeten Dritt-Bibliotheken und deren Lizenzen sind in den Maven Build Dateien (pom.xml) der einzelnen Module ersichtlich und werden am Ende dieses Dokuments zusammengefasst.


# Verwendung des Democodes und der Demokassa

##Ausführen des Codes (Verwenden der Downloadpakete)
Neben dem Source Code wird auch immer eine ZIP Datei der ausführbaren Dateien zur Verfügung gestellt (im Verzeichnis Releases).

###Voraussetzungen
* *Java VM*: Es wird eine aktuelle Java VM (JRE ausreichend) mit Version >= 1.7 benötigt.
* *Kryptographie*: Der Registrierkassen-Demo-Code verwendet starke Kryptographie (z.B. AES mit 256 bit Schlüssel), der mit den Standard-Export Policies der Java VM nicht ausgeführt werden kann. Es muss daher die "Unlimited Strength Policy" von Oracle installiert werden. Siehe: "http://www.oracle.com/technetwork/java/javase/downloads/index.html"

###Verwendung des Demo-Codes - Demokassa
Der Demo Code enthält aktuell eine einfache Testklasse die eine Demokassa ansteuert. Diese Demokassa bietet die Möglichkeit eine angegeben Anzahl von Belegen zu erstellen, diese in das DEP Export Format zu exportieren, und einfache Test-Belege als PDF zu erstellen, die die Daten als QR-Code oder OCR-Code beinhalten.

Download und entpacken von [regkassen-demo-release-0.1.zip](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/release/regkassen-demo-release-0.1.zip)

Ausführen der Demokasse mit

      java -jar regkassen-demo-0.1.jar -o OUTPUT_DIR -n 20
      
Wobei "OUTPUT_DIR" ein Verzeichnis ist, in dem die vom Demo-Code erstellten Daten/Belege geschrieben werden. Wenn die Option "o" nicht angegeben wird, dann wird in aktuellen Verzeichnis eines mit dem Prefix CashBox erstellt.
Die Option "n" gibt die Anzahl der zu erstellenden Belege an. Wenn sie nicht angegeben wird, werden 15 Belege erstellt.
Das Output-Verzeichnis enthält folgende Dateien/Verzeichnisse:

 - **Datei dep-export.txt**: Die generierten Belege im DEP Export Format (Detailspezifikation, Abs 3)
 - **Datei qr-code-rep.txt**: Die textuelle Representation der maschinenlesbaren QR-Codes (der Inhalt der QR-Codes)
 - **Datei ocr-code-rep.txt**: Die textuelle Representation der maschinenlesbaren OCR-Codes
 - **Verzeichnis ocr-code-dir**: PDF-Belege die mit dem OCR-Code bedruckt wurden
 - **Verzeichnis qr-code-dir**: PDF-Belege die mit dem QR-Code bedruckt wurden

Ein Beispiel für den Output ist auch direkt verfügbar: [example-output.zip](https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/release/example-output.zip).

###Verwendung des Prüfwerkzeugs
In dieser Version ist eine erste rudimentäre Version des Prüfwerkzeugs enthalten das die Validität der von einer Kassa erstellten Belege und der Exportdateien überprüft. Hier werden noch detallierte Prüfwerkzeuge angeboten werden, die die korrekte Abbildung von verschiedenen Fällen (z.B. Ausfall Signatureinrichtung) überprüfen und Detailinformationen zur Korrektheit der Formate ausgeben.

       java -jar regkassen-verification-0.1.jar -i DEP-EXPORT-FILE
       
Wobei unter DEP-EXPORT-FILE die im vorigen Beispiel erstellte *dep-export.txt* angegeben wird.
 
##BUILD Prozess, Details zum Code
Das Projekt ist in drei Maven Module aufgeteilt.

 - **regkassen-core**: Dieses Modul enthält den Code der für die Erstellung und Signatur der Belege notwendig ist.
 - **regkassen-democashbox**: Dieses Modul verwendet das regkassen-core Modul und setzt Demo-Use Cases um. Aktuell (Version 0.1) ist dort nur ein Demo enthalten, das Belege erstellt, diese signiert, PDF-Belege erzeugt und die Belege anhand des DEP-Export-Formats ablegt.
 - **regkassen-verification**: Dieses Modul stellt Prüfwerkzeuge zur Verfügung die die Korrektheit der Belege, des Exportformats etc. überprüfen. In der aktuellen Versioen ist hier nur eine sehr rudimentäre Variante enhalten, die eine DEP-Export-Datei auf folgende Aspekte hin überprüft:
	 - kryptographische Gültigkeit der Signatur
	 - kryptographisch korrekte Verkettung der Belege
	 - Zertifikatsprüfungen und Prüfungen der Formate sind noch nicht vorgesehen, werden aber demnächst nachgereicht, um hier die Möglichkeit haben die Korrektheit der Implementierung zu überprüfen.

###Übersicht über den Code
 - **at.sitplus.regkassen.core.base**: Diese Package enthält Basisdatenstrukturen, Hilfsfunktionen und die in der Detailspezifikation definierten RegKassen Suite. (Detailspezifikation, Abs 2).
 - **at.asitplus.regkassen.core.modules**:  In diesem Package sind die Module der Kassa enthalten.
	 - **DEP**: Modul für die Implementierung des Datenerfassungsprotokolls. Wesentliche Funktionalität hier ist der EXPORT des DEPs (implementiert in *SimpleMemoryDEPModule*)
	 - **init**: Parameter für die Initialisierung der Registrierkasse
	 - **print**: Einfache Version eines PDF Druckers, der QR-Codes und OCR-Codes auf Belege druckt.
	 - **signature**: Dieses Package besteht aus zwei Hauptkomponenten:
		 - **jws**: Im SimpleJWSModule werden die JWS Signaturen erstellt (in diesem Fall über eine externe Bibliothek). Das Modul greift auf das folgende Package zu:
		 - **rawsignatureprovider**: Dieses Modul erstellt die wirkliche Signatur und kann eine Smartcard, ein HSM, ein Cloud-Dienst oder ein anderes Modul (im geschlossenen System) darstellen. Wichtige Anmerkung: In der aktuellen DEMO-Kassa ist nur ein simples Software-basiertes Modul enthalten. Dieses DARF AUF KEINEN FALL in einer echten Kasse verwendet werden. In weiteren Demo-Code Versionen wird hier die Ansteuerung der Karte gezeigt.
 - **at.sitplus.regkassen.core**: In diesem Package befindet sich Demo-Registrierkasse (DemoCashBox). Diese Klasse verwendet die oben genannten Module um Belege zu speichern, zu signieren und zu drucken (als PDF).

###Maven Build
Um den Maven Build-Prozess eigenständig durchzuführen sind in den jeweiligen Verzeichnissen folgende Schritte notwendig:

      regkassen-core: mvn install
      regkassen-democashbox: mvn package
      regkassen-verification: mvn package
      
In den Verzeichnissen regkassen-democashbox, regkassen-verification befinden sich nach dem erfolgreichen Build-Prozess die JAR Dateien (im Unterverzeichnis "target"), die zum Ausführen benötigt werden (siehe Punkte zur Verwendung des Demo-Codes weiter oben).

#Erläuterungen zur Detailspezifikation der Verordnung
Es werden hier in den nächsten Tagen Erläuterungen bekannt gegeben, die bestimmte Aspekte der Detailspezifikation betreffen bei denen es noch Interpretationsspielraum gibt. Beispielhaft sind hier genannt:
* Runden der MWST-Beträge beim Addieren zum Umsatzzähler
* Formatierung des DEP-Formats im Jahr 2016
* Formate für die Übertragung der Daten zu Finanzonline
* etc.

Es wird auch noch eine FAQ-Seite erstellt, die Antworten auf oft gestellte Fragen erhält.

#Dritt-Bibliotheken und Lizenzen:

 - Google GSON: Verarbeitung von JSON Elementen
	 - Referenz: https://github.com/google/gson
	 - Lizenz: Apache 2.0, http://www.apache.org/licenses/LICENSE-2.0
 - Bouncycastle Provider: Kryptographie Bibilitothek für JAVA
	 - Referenz: https://www.bouncycastle.org
	 - Lizenz: MIT, http://opensource.org/licenses/MIT
 - JSON Web Signature Library: Für die Erstellung/Prüfung der JSON Web Signaturen
	 - Referenz: https://bitbucket.org/b_c/jose4j
	 - Lizenz: Apache 2.0, http://www.apache.org/licenses/LICENSE-2.0
 - Apache commons libraries for BASE64, BASE64, math operations, etc.
	 - Referenz allgemein: https://commons.apache.org
	 - Lizenz (für alle): Apache 2.0, http://www.apache.org/licenses/LICENSE-2.0
		 - commons-io
		 - commons-math3
		 - commons-codec
		 - commons-cli
 - Google ZXING: QR-Code Erzeugung:
	 - Referenz: http://zxing.github.io/zxing/project-info.html
	 - Lizenz: Apache 2.0, http://www.apache.org/licenses/LICENSE-2.0
 - Apache PDFBOX: PDF Erzeugung (DEMO-Belege)
	 - Referenz: https://pdfbox.apache.org
	 - Lizenz: Apache 2.0, http://www.apache.org/licenses/LICENSE-2.0

OCR-Font: Der OCR-A Font für das Aufbringen des maschinenlesbaren Codes wurde von http://sourceforge.net/projects/ocr-a-font/ bezogen und hat den Lizenz-Typ "Public Domain".
