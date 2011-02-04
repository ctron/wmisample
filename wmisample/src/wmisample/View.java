package wmisample;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart {
	public static final String ID = "wmisample.view";

	private TreeViewer viewer;

	private Text entryField;

	class ViewContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if ( parentElement instanceof Object[])
			{
				return (Object[])parentElement;
			}
			else if ( parentElement instanceof WMIObjectInformation )
			{
				return ((WMIObjectInformation)parentElement).getProperties().entrySet().toArray();
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element) != null;
		}
		
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		
		@Override
		public String getText(Object element) {
			if ( element instanceof WMIObjectInformation )
			{
				return ((WMIObjectInformation)element).getPath();
			}
			return super.getText(element);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return null;
		}
	}

	public void createPartControl(Composite parent) {
		
		parent.setLayout(new GridLayout(2,false));
		this.entryField = new Text (parent, SWT.BORDER );
		entryField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Button queryButton = new Button ( parent, SWT.PUSH );
		queryButton.setText("Query");
		queryButton.addSelectionListener(new SelectionAdapter (){
			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerQuery ();
			}
		});
		
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		// Provide the input to the ContentProvider
		viewer.setInput ( list ( "SELECT * FROM Win32_Processor" ) );
		
		GridData gd = new GridData ( SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan=2;
		viewer.getControl().setLayoutData(gd );
	}

	protected void triggerQuery() {
		try
		{
			viewer.setInput(list(entryField.getText()));
		}
		catch ( Throwable e )
		{
			ErrorDialog.openError(this.getSite().getShell(), "Error", "Failed to query WMI", new Status(IStatus.ERROR,Activator.PLUGIN_ID,"Failed to execute WMI query", e));
		}
	}

	private Object[] list ( String query )
	{
		WMIConnection connection = new WMIConnection();
		try
		{
			return connection.executeQuery(query).toArray();
		}
		finally
		{
			connection.dispose();
		}
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}