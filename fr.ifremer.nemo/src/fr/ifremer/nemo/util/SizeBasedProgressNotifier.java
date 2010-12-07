package fr.ifremer.nemo.util;

import fr.ifremer.nemo.output.ProgressNotification;

/**
 * Class SizeBasedProgressNotifier
 * 
 * 
 * @author Gerard BUNEL
 */
public class SizeBasedProgressNotifier implements Cloneable {
	public long _offset;

	public long _size;

	protected ProgressNotification _progress;

	public SizeBasedProgressNotifier() {
		_progress = null;
	}

	/**
	 * 
	 * @param a_Size
	 * @param a_Offset
	 * @param a_Progress
	 */
	public SizeBasedProgressNotifier(long a_Size, long a_Offset,
			ProgressNotification a_Progress) {
		this();
		_size = a_Size;
		_offset = a_Offset;
		setProgress(a_Progress);

	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	@Override
    public Object clone() throws CloneNotSupportedException {
		SizeBasedProgressNotifier copy = null;
		copy = (SizeBasedProgressNotifier) super.clone();
		copy._progress = null;
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		releaseProgress();
	}

	/**
	 * Read accessor (getter) for attribute _offset
	 * 
	 * @return long
	 * @see SizeBasedProgressNotifier#_offset
	 */
	public long getOffset() {
		return _offset;
	}

	/**
	 * Write accessor (setter) for attribute _offset
	 * 
	 * @param x
	 *            new value for _offset
	 * @see SizeBasedProgressNotifier#_offset
	 */
	public void setOffset(long x) {
		_offset = x;
	}

	/**
	 * Write accessor (setter) for attribute _offset
	 * 
	 * @param a_String
	 *            new value for _offset as String
	 * @see SizeBasedProgressNotifier#_offset
	 */
	public void setOffset(String a_String) {
		try {
			setOffset(Long.parseLong(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _size
	 * 
	 * @return long
	 * @see SizeBasedProgressNotifier#_size
	 */
	public long getSize() {
		return _size;
	}

	/**
	 * Write accessor (setter) for attribute _size
	 * 
	 * @param x
	 *            new value for _size
	 * @see SizeBasedProgressNotifier#_size
	 */
	public void setSize(long x) {
		_size = x;
	}

	/**
	 * Write accessor (setter) for attribute _size
	 * 
	 * @param a_String
	 *            new value for _size as String
	 * @see SizeBasedProgressNotifier#_size
	 */
	public void setSize(String a_String) {
		try {
			setSize(Long.parseLong(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Remove any link for role progress
	 */
	public void releaseProgress() {
		setProgress(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public ProgressNotification getProgress() {
		return _progress;
	}

	/**
	 * Create a new link with x for role progress
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setProgress(ProgressNotification x) {
		if (_progress == x)
			return;
		_progress = x;
	}

	/**
	 * 
	 * @param a_Value
	 *            pourcentage de progression
	 */
	public void progress(long a_Value) {
		if (_progress != null) {
			_progress.progress((int) ((a_Value + _offset) * 100 / _size));
		}
	}
	
	/**
	 * 
	 * @param a_Value
	 *            pourcentage de progression
	 */
	public void progressLine(int line, int total) {
		if (_progress != null && total > 0) {
			_progress.progress(line * 100 / total);
		}
	}

}