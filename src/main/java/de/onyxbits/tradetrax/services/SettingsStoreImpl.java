package de.onyxbits.tradetrax.services;

import java.util.HashMap;
import java.util.List;

import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.hibernate.Session;

import de.onyxbits.tradetrax.entities.Setting;

public class SettingsStoreImpl implements SettingsStore {

	private HashMap<String, Setting> store = new HashMap<String, Setting>();
	private Session session;

	public SettingsStoreImpl(HibernateSessionSource source) {
		this.session = source.create();
		@SuppressWarnings("unchecked")
		List<Setting> lst = (List<Setting>) session.createCriteria(Setting.class).list();
		store = new HashMap<String, Setting>();
		for (Setting s : lst) {
			store.put(s.getName(), s);
		}
	}

	public String get(String key, String value) {
		Setting tmp = store.get(key);
		if (tmp == null) {
			return value;
		}
		else {
			return tmp.getValue();
		}
	}

	public void set(String key, String value) {
		Setting s = store.get(key);
		if (s == null) {
			s = new Setting();
			s.setName(key);
			s.setValue(value);
			store.put(key, s);
			session.beginTransaction();
			session.persist(s);
			session.getTransaction().commit();
		}
		else {
			s.setValue(value);
			session.beginTransaction();
			if (value == null) {
				session.delete(s);
			}
			else {
				session.update(s);
			}
			session.getTransaction().commit();
		}
	}

}
