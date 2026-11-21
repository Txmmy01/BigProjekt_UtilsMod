# UtilsMod -- Projektdokumentation

**Projekt:** UtilsMod\
**Repository:** https://github.com/Txmmy01/BigProjekt_UtilsMod\
**Mod-ID:** `utilsmod`\
**Autor:** Txmmy01\
**Aktueller Stand:** v2.0.0, in `main` gemerged

------------------------------------------------------------------------

## 1. Entwicklungsumgebung

  Bestandteil     Version / Wert
  --------------- ----------------
  Minecraft       26.2
  Fabric Loader   0.20.1
  Fabric API      0.161.0+26.2
  Java            25
  Gradle          9.8.2
  Mod-Version     2.0.0
  Mod-ID          `utilsmod`

Die GitHub-Actions-Workflow-Datei für den Build liegt unter
`.github/workflows/build.yml`.

## 2. Projektregeln

Bei weiteren Änderungen gelten diese Vorgaben:

-   Keine Kommentare oder erklärenden Notizen im Quellcode.
-   Keine Platzhalter und keine halbfertigen Implementierungen.
-   Sauberer, funktionsfähiger Code.
-   In kleinen, kontrollierten Schritten arbeiten.
-   Änderungen prüfen und Fehler beheben, bevor der nächste Schritt
    beginnt.
-   Funktionierenden Bestand möglichst nicht unnötig verändern.
-   Einen Build nur dann als erfolgreich bezeichnen, wenn er tatsächlich
    ausgeführt und erfolgreich abgeschlossen wurde.
-   Keine KI-generierten Bilder oder Texturen in die Mod einbauen.

## 3. Versionsverlauf

### v1.0.0 -- ursprüngliche Basis

Die ursprüngliche Version enthielt:

-   Eine eigene dunkle GUI mit orangefarbenen Akzenten.
-   Die Bereiche Dashboard, Modules und Settings.
-   Öffnen der GUI mit **Right Shift**.
-   Das GUI öffnet sich, ohne das Spiel zu pausieren.
-   BlockGlow mit Umrissen durch Blöcke hindurch.
-   Die BlockGlow-Funktion verändert nicht die Blocktexturen.
-   Einzelne Blocktypen konnten separat aktiviert und farblich angepasst
    werden.
-   Dimensionen, Suche, Reichweite und Intensität wurden berücksichtigt.
-   Einstellungen wurden dauerhaft gespeichert.
-   Konfigurationsdatei: `config/utilsmod.properties`.

Bekannte Referenz der alten v1-Basis: - Commit:
`fc62fd3808d8655dcaf012cc543f0c660f0f660b` - Ein damaliger erfolgreicher
CI-Lauf: `44888306687`

Die v1.0.0-Basis war als Code und Build fertig, wurde aber nicht
vollständig im laufenden Spiel getestet.

### v2.0.0 -- zusätzliche Visual-Module

Auf Wunsch wurden drei eigenständige Module ergänzt:

1.  Fullbright
2.  Clear Lava
3.  Clear Water

Jedes Modul besitzt einen eigenen GUI-Schalter und eine eigene
gespeicherte Einstellung. Die drei Module sind unabhängig voneinander
und standardmäßig deaktiviert.

## 4. Was in v2.0.0 geändert wurde

### Fullbright

Fullbright wurde nicht über eine einfache Gamma-Einstellung umgesetzt.
Stattdessen greift die Implementierung über einen Mixin in den
Lightmap-Renderzustand von Minecraft ein.

Ziel ist eine deutlich hellere Darstellung, ohne nur den Gamma-Wert der
Spieloptionen zu verändern.

### Clear Lava und Clear Water

Für Clear Lava und Clear Water wird ein Mixin in den FogRenderer
verwendet. Damit wird der Flüssigkeitsnebel für die jeweiligen
Flüssigkeiten entfernt.

Wichtig: Die Implementierung soll den Nebel beeinflussen und nicht die
eigentlichen Lava- oder Wassertexturen austauschen.

### Einstellungen und Speicherung

Die drei neuen Module speichern ihren Zustand separat in:

`config/utilsmod-visual.properties`

Die Einstellungen sind unabhängig voneinander. Ein aktiviertes Modul
muss die anderen beiden nicht aktivieren.

### GUI

Die bestehende GUI wurde um drei getrennte Schalter ergänzt:

-   Fullbright
-   Clear Lava
-   Clear Water

Die vorhandene BlockGlow-Funktion sollte durch diese Ergänzungen nicht
verändert werden.

## 5. Relevante Dateien

Im Rahmen von v2.0.0 wurden folgende Dateien neu angelegt, angepasst
oder entfernt:

  -------------------------------------------------------------------------------------------------------
  Datei                                                               Zweck
  ------------------------------------------------------------------- -----------------------------------
  `src/client/java/de/txmmy01/utilsmod/VisualModuleConfig.java`       Speichert die Zustände der drei
                                                                      neuen Visual-Module.

  `src/client/java/de/txmmy01/utilsmod/mixin/FogRendererMixin.java`   Entfernt den Flüssigkeitsnebel für
                                                                      Clear Lava und Clear Water.

  `src/client/java/de/txmmy01/utilsmod/mixin/FullbrightMixin.java`    Greift für Fullbright in den
                                                                      Lightmap-Renderzustand ein.

  `src/main/resources/utilsmod.mixins.json`                           Registriert die Mixins.

  `src/client/java/de/txmmy01/utilsmod/UtilsModScreen.java`           GUI um die drei Modul-Schalter
                                                                      erweitert.

  `src/client/java/de/txmmy01/utilsmod/UtilsModClient.java`           Clientseitige Initialisierung
                                                                      angepasst.

  `src/main/resources/fabric.mod.json`                                Mod-Metadaten und
                                                                      Mixin-Konfiguration angepasst.

  `gradle.properties`                                                 Mod-Version auf `2.0.0` gesetzt.

  Übersetzungsdateien `en_us` und `de_de`                             Texte für die neuen GUI-Elemente
                                                                      ergänzt.

  `.github/workflows/build.yml`                                       Build-Workflow angepasst.

  `README.md`                                                         Projektdokumentation aktualisiert.

  `ROADMAP.md`                                                        Roadmap aktualisiert.
  -------------------------------------------------------------------------------------------------------

Die alte gamma-basierte `FullbrightModule.java` wurde entfernt und durch
die Mixin-basierte Lösung ersetzt.

## 6. Technischer Fix während der Entwicklung

Ein Build schlug zunächst wegen eines ungültigen Alpha-Setters auf einem
`Vector4f` fehl.

Fehlerhafte Zeile:

``` java
fog.color.w(0.0f);
```

Korrigierte Zeile:

``` java
fog.color.set(fog.color.x(), fog.color.y(), fog.color.z(), 0.0f);
```

Der Fix wurde mit folgendem Commit eingecheckt:

-   Commit: `e23e2e6c266e85c674f22c97f75f32143ba09258`
-   Commit-Nachricht: `Fix liquid fog color alpha setter`

## 7. Git- und Pull-Request-Verlauf

Entwicklungsbranch:

`v2.0.0-development`

Pull Request:

https://github.com/Txmmy01/BigProjekt_UtilsMod/pull/1

Titel:

`Develop UtilsMod v2.0.0 visual modules`

Der Pull Request wurde gegen `main` erstellt. Nach erfolgreicher
Build-Prüfung wurde er aus dem Draft-Status geholt und anschließend auf
Wunsch gemerged.

### Erfolgreiche Build-Prüfungen

Auf dem finalen Entwicklungsstand liefen beide relevanten
GitHub-Actions-Läufe erfolgreich:

-   Pull-Request-Workflow `44889094297` -- abgeschlossen, Ergebnis
    `success`.
-   Push-Workflow `44889089723` -- abgeschlossen, Ergebnis `success`.

Erfolgreicher Workflow:

https://github.com/Txmmy01/BigProjekt_UtilsMod/actions/runs/44889094297

### Merge

Der Pull Request wurde per **Squash Merge** in `main` übernommen.

GitHub bestätigte:

-   `merged: true`
-   Merge-Commit: `ec0e1e39be0833872e5822ac3ab7cb9b96ce3865`

Damit befindet sich v2.0.0 auf `main`.

## 8. Was bereits geprüft wurde -- und was noch offen ist

### Geprüft

-   Der finale Stand von v2.0.0 wurde von GitHub Actions erfolgreich
    gebaut.
-   Der Pull-Request-Workflow war erfolgreich.
-   Der Push-Workflow war erfolgreich.
-   Der Pull Request wurde erfolgreich in `main` gemerged.

### Noch nicht durch einen Ingame-Test bestätigt

Ein erfolgreicher Build beweist, dass der Code kompiliert und das
Build-Artefakt erstellt werden kann. Er beweist **nicht**, dass jedes
Modul im tatsächlichen Minecraft-Spiel korrekt funktioniert.

Insbesondere müssen noch im Spiel geprüft werden:

-   Öffnet **Right Shift** weiterhin die GUI?
-   Funktioniert BlockGlow unverändert?
-   Lässt sich Fullbright einzeln ein- und ausschalten?
-   Werden Lava-Nebel und Wasser-Nebel jeweils separat entfernt?
-   Bleiben die drei Zustände nach einem Neustart erhalten?
-   Funktionieren die Module in den relevanten Dimensionen und
    Spielsituationen?
-   Gibt es Laufzeitfehler oder Mixin-Konflikte mit anderen Mods?

Die Module sind implementiert und der Build ist erfolgreich, aber ein
vollständiger Laufzeittest wurde bisher nicht bestätigt.

## 9. Hinweise für den ersten Test

1.  Eine Sicherung der vorhandenen Konfiguration anlegen.
2.  Minecraft mit der passenden Version und Fabric-Umgebung starten.
3.  Prüfen, ob die Mod ohne Mixin- oder Startfehler lädt.
4.  Mit **Right Shift** die GUI öffnen.
5.  Jedes der drei neuen Module einzeln aktivieren und testen.
6.  Die Kombinationen testen, bei denen mehrere Module gleichzeitig
    aktiv sind.
7.  Spiel schließen und erneut starten, um die Speicherung zu prüfen.
8.  BlockGlow separat kontrollieren, damit unbeabsichtigte Änderungen
    auffallen.

Bei Fehlern sollten `latest.log`, der genaue Spielstand und die
aktivierten Module dokumentiert werden. So lässt sich ein Problem
gezielt einer Funktion zuordnen.

## 10. Wichtige Links

-   Repository: https://github.com/Txmmy01/BigProjekt_UtilsMod
-   Pull Request #1:
    https://github.com/Txmmy01/BigProjekt_UtilsMod/pull/1
-   Erfolgreicher Build:
    https://github.com/Txmmy01/BigProjekt_UtilsMod/actions/runs/44889094297

## 11. Aktueller Status in einem Satz

**UtilsMod v2.0.0 mit Fullbright, Clear Lava und Clear Water ist
erfolgreich gebaut und in `main` gemerged; der vollständige
Funktionstest im laufenden Minecraft-Spiel steht noch aus.**
