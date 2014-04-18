package com.lachlanhurst.client.userInterface;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TemporaryWarningDialogBox extends DialogBox 
{
	public static String COOKIE_HAS_DISPLAYED_WARNING = "BikeBingleDisplayedDev_4";
	
	public TemporaryWarningDialogBox()
	{
		init();
	}
	
	private void init()
	{
		this.ensureDebugId("cwDialogBoxWarn");
		this.setText("BikeBingle");
		//this.setStylePrimaryName("dial");
		this.addStyleName("dial");
		
		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		this.setWidget(dialogContents);

		// Add some text to the top of the dialog
		String htmlS = 
		"Please note: this version of BikeBingle is in under testing.</br>" +
		"All data in DB will be deleted soon";
		
		
		HTML details = new HTML(htmlS);
		details.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		dialogContents.add(details);
		dialogContents.setCellHorizontalAlignment(details, HasHorizontalAlignment.ALIGN_CENTER);

		// Add an image to the dialog
		Image image;
		image = new Image("dialog-error.png");
		dialogContents.add(image);
		dialogContents.setCellHorizontalAlignment(image,HasHorizontalAlignment.ALIGN_CENTER);

		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("Ok",new ClickListener() 
		{
			public void onClick(Widget sender) {
				setCookie();
				doHide();
			}
		});
		closeButton.setStylePrimaryName("addStackPanel-Button");
		
		dialogContents.add(closeButton);
		if (LocaleInfo.getCurrentLocale().isRTL()) {
			dialogContents.setCellHorizontalAlignment(closeButton,
					HasHorizontalAlignment.ALIGN_LEFT);

		} else {
			dialogContents.setCellHorizontalAlignment(closeButton,
					HasHorizontalAlignment.ALIGN_RIGHT);
		}

	}
	
	private void doHide()
	{
		this.hide();
	}
	
	private void setCookie()
	{
		Cookies.setCookie(COOKIE_HAS_DISPLAYED_WARNING, "shown", MapMoveCookieSaver.getExpiryDateForCookie());
	}
	
	public static boolean isToBeShown()
	{
		//Cookies.setCookie(COOKIE_HAS_DISPLAYED_WARNING, value, expiry);
		
		String cookie = Cookies.getCookie(COOKIE_HAS_DISPLAYED_WARNING);
		if (cookie == null)
		{
			return true;
			//return false;
		}
		else
		{
			return false;
		}
	}
}
