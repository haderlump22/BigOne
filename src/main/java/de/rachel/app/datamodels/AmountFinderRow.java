package de.rachel.app.datamodels;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

public class AmountFinderRow {
  private final IntegerProperty transactionID = new SimpleIntegerProperty();
  private final StringProperty cdtDbtIndicator = new SimpleStringProperty();
  private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
  private final DoubleProperty amount = new SimpleDoubleProperty();
  private final StringProperty comment = new SimpleStringProperty();
  private final ObjectProperty<LocalDate> billingMonth = new SimpleObjectProperty<>();
  private final StringProperty bookingEvent = new SimpleStringProperty();
}
