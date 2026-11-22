package de.rachel.app.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class InputFormatters {
  public static final UnaryOperator<TextFormatter.Change> DECIMAL_MASK_FILTER = change -> {
    String newText = change.getControlNewText();

    if (change.isDeleted()) {
      return change;
    }

    // Erlaube leeres Feld oder reine Ziffern mit maximal einem Komma
    if (newText.matches("^\\d*[,]?\\d*$")) {
      try {
        // Konvertiere das Komma für Java in einen Punkt, falls vorhanden
        String parseable = newText.replace(",", ".");

        // Wenn nur ein Punkt/Komma dasteht (z.B. "." oder ","), erlauben
        if (parseable.equals(".") || parseable.isEmpty()) {
          return change;
        }

        // pharsen testen
        Double.parseDouble(parseable);

        return change;
      } catch (NumberFormatException e) {
        // Falls das Parsen fehlschlägt, Änderung verwerfen
      }
    }

    return null;
  };

  public static final UnaryOperator<TextFormatter.Change> DATE_MASK_FILTER = change -> {
    if (change.isDeleted() || change.getControlNewText().isEmpty()) {
      return change;
    }

    String fullText = change.getControlNewText();
    String newText = change.getText();

    // Maximale Länge auf 10 Zeichen begrenzen (DD-MM-20YY)
    if (fullText.length() > 10) {
      return null;
    }

    // nur Ziffern erlauben
    if (!newText.matches("\\d*")) {
      return null;
    }

    // automatisches setzen der zwischenzeichen
    if (fullText.length() == 2) {
      change.setText(newText + ".");

      // Die exakte neue Position berechnen (Start der Eingabe + Länge des neuen
      // Textes inkl. Punkt)
      int newPos = change.getRangeStart() + change.getText().length();

      // Caret UND Anchor auf dieselbe Position setzen (verhindert das Markieren)
      change.setCaretPosition(newPos);
      change.setAnchor(newPos);
    }

    // automatisches setzen der zwischenzeichen
    if (fullText.length() == 5) {
      change.setText(newText + ".20");

      // Die exakte neue Position berechnen (Start der Eingabe + Länge des neuen
      // Textes inkl. Punkt)
      int newPos = change.getRangeStart() + change.getText().length();

      // Caret UND Anchor auf dieselbe Position setzen (verhindert das Markieren)
      change.setCaretPosition(newPos);
      change.setAnchor(newPos);
    }

    return change; // Änderung erlauben
  };

  public static final StringConverter<Double> GERMAN_DOUBLE_CONVERTER = new StringConverter<>() {
    private final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);

    @Override
    public String toString(Double value) {
      return value == null ? "" : nf.format(value);
    }

    @Override
    public Double fromString(String text) {
      if (text == null || text.isBlank()) {
        return null;
      }
      try {
        Number parsed = nf.parse(text.trim().replace('.', ','));
        return parsed.doubleValue();
      } catch (ParseException e) {
        throw new IllegalArgumentException("Ungültige Zahl: " + text, e);
      }
    }
  };

  private InputFormatters() {
  }
}
