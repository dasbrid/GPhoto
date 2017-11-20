package asbridged.me.uk.gphoto.classes;

import java.io.File;

/**
 * Created by AsbridgeD on 17/11/2015.
 */
public class CheckedFile {
    private File file;
    private boolean checked;

    public CheckedFile(File file)
    {
        this.file = file;
        this.checked = false;
    }
    public File getFile() { return this.file;}
    public boolean isChecked() { return this.checked; }
    public void setChecked(boolean state) { this.checked = state; }

    @Override
    public boolean equals (Object o)
    {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }
        if (!(o instanceof CheckedFile)) {
            return false;
        }
        CheckedFile c = (CheckedFile) o;
        return (c.getFile() == this.getFile());
    }

}
