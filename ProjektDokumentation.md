# UtilsMod – Projektdokumentation

**Mod-ID:** utilsmod  
**Repository:** https://github.com/Txmmy01/BigProjekt_UtilsMod  
**Maintainer:** Txmmy01  
**Dokumentierter Release-Stand:** 2.1.0  
**Aktuelle Verbesserungen:** Pull Request #3 (noch nicht gemerged)

## Entwicklungsumgebung

| Bestandteil | Version |
|---|---|
| Minecraft | 26.2 |
| Fabric Loader | 0.20.1 |
| Fabric API | 0.161.0+26.2 |
| Java | 25 |
| Gradle | 9.8.2 |

## Funktionen

- BlockGlow für ausgewählte Erze, Spawner und Truhen mit getrennten Farben und Dimensionseinstellungen.
- Fullbright über den Lightmap-Renderzustand.
- Clear Water und Clear Lava.
- Armor HUD, Potion Bars, FPS HUD und CPS-Zähler.
- Einstellungen werden in getrennten Properties-Dateien im Minecraft-Konfigurationsordner gespeichert.
- GUI öffnen/schließen: Right Shift.

## Build

Build-Befehl: gradle --no-daemon build

Das JAR liegt anschließend unter build/libs/. GitHub Actions baut Pull Requests gegen main und Pushes auf main.

## Aktueller Wartungsstand

Pull Request #3:
https://github.com/Txmmy01/BigProjekt_UtilsMod/pull/3

Der PR verbessert die BlockGlow-Suche, CPS-Eingabeerfassung, HUD-Anzeigen, GUI-Scrollfunktion und Konfigurationsspeicherung. Der GitHub-Actions-Build des Stands vom 21.11.2026 war erfolgreich.

## Teststatus

Der Build prüft Kompilierung und Paketierung. Er ersetzt keinen Test im laufenden Minecraft-Client. Vor einem Release müssen insbesondere GUI-Größen, Rendering, Dimensionen, Mixin-Kompatibilität und das Verhalten mit anderen Client-Mods praktisch geprüft werden.
