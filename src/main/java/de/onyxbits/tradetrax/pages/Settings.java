package de.onyxbits.tradetrax.pages;

import java.util.Currency;
import java.util.Locale;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import de.onyxbits.tradetrax.remix.MoneyRepresentation;
import de.onyxbits.tradetrax.services.SettingsStore;

public class Settings {

	@Property
	private String financialFormLedgerTitle;

	@Property
	@Validate(value="required")
	private String financialFormCurrencySymbol;

	@Property
	@Validate(value="min=0")
	private int financialFormDecimals;

	@Property
	private boolean uiFormHideInstructions;

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

	@Inject
	private Session session;

	@Inject
	private Messages messages;

	@Inject
	private SettingsStore settingsStore;

	public void setupRender() {
		MoneyRepresentation mr = new MoneyRepresentation(settingsStore);
		financialFormLedgerTitle = settingsStore.get(SettingsStore.LEDGERTITLE, null);
		financialFormCurrencySymbol = mr.getCurrencySymbol();
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
	}

	public void onSuccessFromFinancialForm() {
		settingsStore.set(SettingsStore.LEDGERTITLE, financialFormLedgerTitle);
		settingsStore.set(SettingsStore.CURRENCYSYMBOL, financialFormCurrencySymbol);
		settingsStore.set(SettingsStore.DECIMALS, financialFormDecimals + "");
	}

	public void onSuccessFromUiForm() {
		settingsStore.set(SettingsStore.HIDEINSTRUCTIONS, uiFormHideInstructions + "");
	}
}
