package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class DirVar extends ScalarVar<File> {

	public DirVar(String name, File value) {
		super(name, value);
	}

	public DirVar(String name, File value, boolean nullable) {
		super(name, value, nullable);
	}

	@Override
	protected File fromString(String arg) throws GricliSetValueException {
		if (arg == null){
			throw new GricliSetValueException(getName(), "null","dir cannot be unset");
		}
		try {
			//expand path for checking
			String resultValue = StringUtils.replace(
					arg, "~", System.getProperty("user.home"));
			File dir = new File(resultValue);

			if (!dir.isAbsolute()) {
				dir = new File(System.getProperty("user.dir"), arg);
			}
			//check path
			if (!dir.exists()) {
				throw new GricliSetValueException(getName(),
						dir.getCanonicalPath(), "directory does not exist");
			}

			return dir;
		} catch (IOException ex) {
			throw new GricliSetValueException(getName(), arg, ex.getMessage());
		}
	}

	@Override
	public String toString(){
		boolean windows = System.getProperty("file.separator").equals("\\");
		try {
			String value = get().getCanonicalPath();
			if (windows){
				value = value.replace("\\", "/");
				String winhome = System.getProperty("user.home").replace("\\", "/"); 
				if (value.startsWith(winhome)){
					return value.replaceFirst(winhome, "~");			
				} else {
					return value.replace("\\", "/");
				}
			} else {
				if (value.startsWith(System.getProperty("user.home"))){
					return value.replaceFirst(System.getProperty("user.home"), "~");				
				} else {
					return value;
				}
			}
		} catch (IOException e) {
			return null;
		}

	}


}
