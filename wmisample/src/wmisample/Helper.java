package wmisample;

import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.IEnumVARIANT;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;

public class Helper {

	public static int getId(OleAutomation auto, String name) {
		int result[] = auto.getIDsOfNames(new String[]{name});
		if ( result == null || result.length < 1 )
		{
			throw new RuntimeException ( String.format ("Object does not support '%s'", name )  );
		}
		return result[0];
	}
	
	public static interface VariantVisitor
	{
		public void visit ( Variant variant );
	}

	public static int forEachVariant ( Variant enumerable, VariantVisitor variantVisitor )
	{
		OleAutomation enumerableAuto = enumerable.getAutomation();
		
		try
		{
			Variant enumObject = enumerableAuto.getProperty(Helper.getId(enumerableAuto, "_NewEnum"));
			
			long /*int*/ [] ppvObject = new long /*int*/ [1];
			int rc  = enumObject.getUnknown().QueryInterface(COM.IIDIEnumVARIANT, ppvObject );

			if ( rc != OS.S_OK )
				return rc;
			
			IEnumVARIANT enumVariant = new IEnumVARIANT(ppvObject[0]);
			
			try
			{
				enumVariant.Reset();
				
				int [] pceltFetched = new int[1];
				
				long rgelt = OS.GlobalAlloc(OS.GMEM_FIXED | OS.GMEM_ZEROINIT, Variant.sizeof);
				
				try
				{
					while (enumVariant.Next(1, rgelt, pceltFetched) == COM.S_OK && pceltFetched[0] == 1) {
						Variant v = Variant.win32_new(rgelt);
						variantVisitor.visit ( v );
					}
				}
				finally
				{
					OS.GlobalFree(rgelt);	
				}
			}
			finally
			{
				enumVariant.Release();
			}
			
			return OS.S_OK;
			
		}
		finally
		{
			enumerableAuto.dispose();
		}
		
	}
	

	public static Object convertVariant ( Variant variant )
	{
		if ( variant == null )
			return null;
		
		short type = variant.getType();
		switch ( type )
		{
		case COM.VT_EMPTY:
		case COM.VT_NULL:
			return null;
		case COM.VT_BOOL:
			return variant.getBoolean();
			
		case COM.VT_I1:
			return variant.getByte ();
		case COM.VT_I2:
			return variant.getShort ();
		case COM.VT_I4:
			return variant.getInt();
		case COM.VT_I8:
			return variant.getLong();
			
		case COM.VT_UI1:
			return variant.getChar();
		case COM.VT_UI2:
			return variant.getShort ();
		case COM.VT_UI4:
			return variant.getInt();
			
		case COM.VT_BSTR:
			return variant.getString ();
			
		case COM.VT_R4:
			return variant.getFloat();
		case COM.VT_R8:
			return variant.getDouble();
			
		// FIXME: add some more
			
		default:
			return variant.toString();
		}
	}

	public static Variant getParameter(Variant variant, String string) {
		OleAutomation auto = variant.getAutomation();
		try
		{
			return auto.getProperty(getId(auto , string));
		}
		finally
		{
			auto.dispose();
		}
	}
}
