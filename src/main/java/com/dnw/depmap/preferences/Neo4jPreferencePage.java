package com.dnw.depmap.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.dnw.depmap.Activator;
import com.dnw.plugin.preference.GroupFieldEditor;
import com.dnw.plugin.preference.TextFieldEditor;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace
 * that allows us to create a page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 * 
 * @author manbaum
 * @since Sep 2, 2015
 */
public class Neo4jPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private GroupFieldEditor gf1;
	private BooleanFieldEditor flagurl;
	private StringFieldEditor dburl;
	private BooleanFieldEditor flagdir;
	private DirectoryFieldEditor dbdir;

	private GroupFieldEditor gf3;
	private BooleanFieldEditor flagexec_pre;
	private TextFieldEditor statements_pre;
	private BooleanFieldEditor flagexec_post;
	private TextFieldEditor statements_post;

	public Neo4jPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Neo4j graph database settings.\n");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
	 * manipulate various types of preferences. Each field editor knows how to save and restore
	 * itself.
	 */
	public void createFieldEditors() {
		final Composite p = getFieldEditorParent();

		gf1 = new GroupFieldEditor("Neo4j graph database", p);
		flagurl = new BooleanFieldEditor(PrefKeys.P_USESTANDALONE,
				"Standalone database server (access thru Rest API)", gf1.getGroupControl(p));
		dburl = new StringFieldEditor(PrefKeys.P_DBURL, "Url:", gf1.getGroupControl(p));
		flagdir = new BooleanFieldEditor(PrefKeys.P_USEEMBEDDED,
				"Embedded database server (access thru Java API)", gf1.getGroupControl(p));
		dbdir = new DirectoryFieldEditor(PrefKeys.P_DBDIR, "Store:", gf1.getGroupControl(p));

		gf3 = new GroupFieldEditor("Additional cypher statements (one statement each line)", p);
		flagexec_pre = new BooleanFieldEditor(PrefKeys.P_USEPREEXEC,
				"Enable executing statements before generating", gf3.getGroupControl(p));
		statements_pre = new TextFieldEditor(PrefKeys.P_PREEXEC, "", 56, 10, gf3.getGroupControl(p));
		flagexec_post = new BooleanFieldEditor(PrefKeys.P_USEPOSTEXEC,
				"Enable executing statements after generating", gf3.getGroupControl(p));
		statements_post = new TextFieldEditor(PrefKeys.P_POSTEXEC, "", 56, 10,
				gf3.getGroupControl(p));

		gf1.addField(flagurl);
		gf1.addField(dburl);
		gf1.addField(flagdir);
		gf1.addField(dbdir);
		addField(gf1);

		gf3.addField(flagexec_pre);
		gf3.addField(statements_pre);
		gf3.addField(flagexec_post);
		gf3.addField(statements_post);
		addField(gf3);
	}

	/**
	 * Initializes all field editors.
	 * 
	 * @author manbaum
	 * @since Sep 2, 2015
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#initialize()
	 */
	@Override
	protected void initialize() {
		super.initialize();
		Composite p = getFieldEditorParent();
		boolean value1 = flagurl.getBooleanValue();
		dburl.setEnabled(value1, gf1.getGroupControl(p));
		boolean value2 = flagurl.getBooleanValue();
		dbdir.setEnabled(!value2, gf1.getGroupControl(p));
		boolean value5 = flagexec_pre.getBooleanValue();
		statements_pre.setEnabled(value5, gf3.getGroupControl(p));
		boolean value6 = flagexec_post.getBooleanValue();
		statements_post.setEnabled(value6, gf3.getGroupControl(p));
	}

	/**
	 * The field editor preference page implementation of this <code>IPreferencePage</code> (and
	 * <code>IPropertyChangeListener</code>) method intercepts <code>IS_VALID</code> events but
	 * passes other events on to its superclass.
	 * 
	 * @author manbaum
	 * @since Sep 2, 2015
	 * @param event the property change event object describing which property changed and how.
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		FieldEditor f = (FieldEditor)event.getSource();
		Composite p = getFieldEditorParent();
		if (event.getProperty() == FieldEditor.VALUE) {
			if (f.getPreferenceName().equals(PrefKeys.P_USESTANDALONE)) {
				boolean value = (Boolean)event.getNewValue();
				dburl.setEnabled(value, gf1.getGroupControl(p));
			} else if (f.getPreferenceName().equals(PrefKeys.P_USEEMBEDDED)) {
				boolean value = (Boolean)event.getNewValue();
				dbdir.setEnabled(value, gf1.getGroupControl(p));
			} else if (f.getPreferenceName().equals(PrefKeys.P_USEPREEXEC)) {
				boolean value = (Boolean)event.getNewValue();
				statements_pre.setEnabled(value, gf3.getGroupControl(p));
			} else if (f.getPreferenceName().equals(PrefKeys.P_USEPOSTEXEC)) {
				boolean value = (Boolean)event.getNewValue();
				statements_post.setEnabled(value, gf3.getGroupControl(p));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
}