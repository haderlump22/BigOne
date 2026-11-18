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
  private final IntegerProperty transactionId = new SimpleIntegerProperty();
  private final StringProperty cdtDbtIndicator = new SimpleStringProperty();
  private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
  private final DoubleProperty amount = new SimpleDoubleProperty();
  private final StringProperty comment = new SimpleStringProperty();
  private final ObjectProperty<LocalDate> billingMonth = new SimpleObjectProperty<>();
  private final StringProperty bookingEvent = new SimpleStringProperty();

  public AmountFinderRow(int transactionID, String cdtDbtIndicator, LocalDate date, Double amount, String comment,
      LocalDate billingMonth, String bookingEvent) {
    this.transactionId.set(transactionID);
    this.cdtDbtIndicator.set(cdtDbtIndicator);
    this.date.set(date);
    this.amount.set(amount);
    this.comment.set(comment);
    this.billingMonth.set(billingMonth);
    this.bookingEvent.set(bookingEvent);
  }

  public int getTransactionId() {return transactionId.get();}
  public IntegerProperty transactionIdProperty() {return transactionId;}

  public String getCdtDbtIndicator() {return cdtDbtIndicator.get();}
  public StringProperty cdtDbtIndicatorProperty() {return cdtDbtIndicator;}

  public LocalDate getDate() {return date.get();}
  public ObjectProperty<LocalDate> dateProperty() {return date;}
  public void setDate(LocalDate date) {this.date.set(date);}

  public Double getAmount() {return amount.get();}
  public DoubleProperty amountProperty() {return amount;}
  public void setAmount(Double amount) {this.amount.set(amount);}

  public String getComment() {return comment.get();}
  public StringProperty commentProperty() {return comment;}
  public void setComment(String comment) {this.comment.set(comment);}

  public LocalDate getBillingMonth() {return billingMonth.get();}
  public ObjectProperty<LocalDate> billingMonthProperty() {return billingMonth;}
  public void setBillingmonth(LocalDate billingMonth) {this.billingMonth.set(billingMonth);}

  public String getBookingEvent() {return bookingEvent.get();}
  public StringProperty bookingEventProperty() {return bookingEvent;}
}
