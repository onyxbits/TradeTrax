package de.onyxbits.tradetrax.pages;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.util.EnumSelectModel;
import org.apache.tapestry5.util.EnumValueEncoder;

import de.onyxbits.tradetrax.remix.AcquisitionFields;
import de.onyxbits.tradetrax.remix.LedgerColumns;
import de.onyxbits.tradetrax.services.MoneyRepresentation;
import de.onyxbits.tradetrax.services.SettingsStore;

public class Settings {

	@Property
	private String financialFormLedgerTitle;

	@Property
	@Validate(value = "required")
	private String financialFormCurrencySymbol;

	@Property
	@Validate(value = "min=0")
	private int financialFormDecimals;

	@Property
	private boolean uiFormHideInstructions;

	@Property
	private boolean uiFormShowCalculator;

	@Property
	@Validate(value = "min=25")
	private int resultsPerPage = 25;

	@Component(id = "financialForm")
	private Form financialForm;

	@Component(id = "financialFormLedgerTitle")
	private TextField financialFormLedgerTitleField;

	@Component(id = "financialFormDecimals")
	private TextField financialFormDecimalsField;

	@Component(id = "financialFormCurrencySymbol")
	private TextField financialFormCurrencySymbolField;

	@Component(id = "uiForm")
	private Form uiForm;

	@Component(id = "uiFormHideInstructions")
	private Checkbox uiFormHideInstructionsField;

	@Component(id = "uiFormShowCalculator")
	private Checkbox uiFormShowCalculatorField;

	@Inject
	private Messages messages;

	@Inject
	private SettingsStore settingsStore;
	
	@Inject
	private MoneyRepresentation moneyRepresentation;

	@Inject
	private TypeCoercer typeCoercer;

	@Component(id = "tcForm")
	private Form tcForm;

	@Property
	private final ValueEncoder<LedgerColumns> ledgerColumnsEncoder = new EnumValueEncoder<LedgerColumns>(
			typeCoercer, LedgerColumns.class);

	@Property
	private List<LedgerColumns> ledgerColumnsList = new Vector<LedgerColumns>();

	@Property
	private final SelectModel acquisitionFieldsModel = new EnumSelectModel(AcquisitionFields.class,
			messages);

	@Property
	private final ValueEncoder<AcquisitionFields> acquisitionFieldsEncoder = new EnumValueEncoder<AcquisitionFields>(
			typeCoercer, AcquisitionFields.class);

	@Property
	private List<AcquisitionFields> acquisitionFieldsList = new Vector<AcquisitionFields>();

	@Property
	private final SelectModel ledgerColumnsModel = new EnumSelectModel(LedgerColumns.class, messages);

	public void setupRender() {
		financialFormLedgerTitle = settingsStore.get(SettingsStore.LEDGERTITLE, null);
		financialFormCurrencySymbol = moneyRepresentation.getCurrencySymbol();
		financialFormDecimals = Currency.getInstance(Locale.getDefault()).getDefaultFractionDigits();
		try {
			financialFormDecimals = Integer.parseInt(settingsStore.get(SettingsStore.DECIMALS, null));
		}
		catch (Exception e) {
		}
		try {
			String tmp = settingsStore.get(SettingsStore.HIDEINSTRUCTIONS, null);
			uiFormHideInstructions = Boolean.parseBoolean(tmp);
		}
		catch (Exception e) {
		}
		try {
			String tmp = settingsStore.get(SettingsStore.SHOWCALCULATOR, null);
			uiFormShowCalculator = Boolean.parseBoolean(tmp);
		}
		catch (Exception e) {
		}
		try {
			ledgerColumnsList = LedgerColumns.fromCsv(settingsStore.get(SettingsStore.TCLCOLUMNS,
					LedgerColumns.DEFAULT));
		}
		catch (Exception e) {

		}
		try {
			acquisitionFieldsList = AcquisitionFields.fromCsv(settingsStore.get(
					SettingsStore.TCACFIELDS, AcquisitionFields.DEFAULT));
		}
		catch (Exception e) {

		}
	}

	public void onSuccessFromFinancialForm() {
		settingsStore.set(SettingsStore.LEDGERTITLE, financialFormLedgerTitle);
		settingsStore.set(SettingsStore.CURRENCYSYMBOL, financialFormCurrencySymbol);
		settingsStore.set(SettingsStore.DECIMALS, financialFormDecimals + "");
	}

	public void onSuccessFromUiForm() {
		settingsStore.set(SettingsStore.HIDEINSTRUCTIONS, uiFormHideInstructions + "");
		settingsStore.set(SettingsStore.SHOWCALCULATOR, uiFormShowCalculator + "");
	}

	public void onValidateFromTcForm() {
		if (ledgerColumnsList.size() == 0) {
			tcForm.recordError(messages.get("error-empty-ledger"));
		}
	}

	public void onSuccessFromTcForm() {
		settingsStore.set(SettingsStore.TCLCOLUMNS, LedgerColumns.toCsv(ledgerColumnsList));
		settingsStore.set(SettingsStore.TCACFIELDS, AcquisitionFields.toCsv(acquisitionFieldsList));
	}
}
