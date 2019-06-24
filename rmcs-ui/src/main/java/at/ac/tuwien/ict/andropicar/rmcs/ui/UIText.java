package at.ac.tuwien.ict.andropicar.rmcs.ui;

/**
 * Contains a String and provides the ability to alter that String depending on the value of {@link #textstyle} and {@link #maxLogLength}.
 * @author Boeck
 */
public class UIText {

	/** The manner the text should be updated in. */
	private ETextStyle textstyle;
	
	/** The maximum length the text may be, if it is configured to be updated in a log-style format.  */
	private int maxLogLength;
	
	/** The String containing the text. */
	private String text;
	
	/**
	 * @param textStyle the manner the text should be updated in.
	 * @param maxLogLength the maximum length the text may be, if it is configured to be updated in a log-style format.
	 * @param text the String containing the text.
	 */
	public UIText(ETextStyle textStyle, int maxLogLength, String text) {
		this.textstyle = textStyle;
		this.maxLogLength = maxLogLength;
		this.text = text;
	}

	
	/**
	 * @return the manner the text should be updated in.
	 */
	public ETextStyle getTextStyle() {
		return this.textstyle;
	}

	/**
	 * @param textStyle the manner the text should be updated in.
	 */
	public void setTextStyle(ETextStyle textStyle) {
		if(textStyle == null)
			return;
		
		this.textstyle = textStyle;
	}

	/**
	 * @return the maximum length the text may be, if it is configured to be updated in a log-style format.
	 */
	public int getMaxLogLength() {
		return this.maxLogLength;
	}

	/**
	 * @param maxLogLength the maximum length the text may be, if it is configured to be updated in a log-style format.
	 */
	public void setMaxLogLength(int maxLogLength) {
		this.maxLogLength = maxLogLength;
	}

	/**
	 * @return the String containing the text.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Updates {@link #text}, based on {@link #textstyle}.
	 * If it is {@link ETextStyle#FIXEDSTYLE}, the passed String will overwrite {@link #text}.
	 * If it is {@link ETextStyle#LOGSTYLE}, the passed String will be appended to {@link #text}.
	 * If the new String is longer than {@link #maxLogLength}, Characters will be removed from the beginning of {@link #text}, until it is as long as {@link #maxLogLength}.
	 * @param text the String containing the new text.
	 */
	public void setText(String text) {
		if(text == null)
			return;
		
		if(getTextStyle().equals(ETextStyle.FIXEDSTYLE))
			this.text = text;
		else {
			if(getText().length() + text.length() - getMaxLogLength() > 0)
			{
				this.text = getText().substring((getText().length() + text.length() - getMaxLogLength()), getText().length()).concat(text);
				// cut text to next newline
				this.text = getText().substring(getText().indexOf('\n'), getText().length());
			}
			else
				this.text = this.text.concat(text);
		}
	}
	
}
