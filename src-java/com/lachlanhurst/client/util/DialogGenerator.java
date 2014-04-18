package com.lachlanhurst.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * class with a few helper methods that create some dialogs
 * @author lachlan
 *
 */
public class DialogGenerator 
{
	public static DialogBox createExceptionDialogBox(Exception e) 
	{
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwDialogBox");
		dialogBox.setText("BikeBingle");

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		
		String htmlS = "<b>" +e.getMessage()+ "</b> </br>";
		htmlS += "</br>";
		//StackTraceElement[] stes =  e.getStackTrace();
		//for (int i = 0; i < stes.length; i++)
		//{
			GWT.log("error", e);
		//}
		
		// Add some text to the top of the dialog
		HTML details = new HTML(htmlS);
		dialogContents.add(details);
		dialogContents.setCellHorizontalAlignment(details, HasHorizontalAlignment.ALIGN_CENTER);

		// Add an image to the dialog
		Image image = new Image("dialog-error.png");;

		dialogContents.add(image);
		dialogContents.setCellHorizontalAlignment(image,HasHorizontalAlignment.ALIGN_CENTER);

		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("Ok",
				new ClickListener() {
			public void onClick(Widget sender) {
				dialogBox.hide();
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

		// Return the dialog box
		return dialogBox;
	}
	
	public static DialogBox createErrorDialogBox(String htmlErrorMsg) 
	{
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwDialogBox");
		dialogBox.setText("BikeBingle");

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		
		String htmlS = htmlErrorMsg;
		
		// Add some text to the top of the dialog
		HTML details = new HTML(htmlS);
		dialogContents.add(details);
		dialogContents.setCellHorizontalAlignment(details, HasHorizontalAlignment.ALIGN_CENTER);

		// Add an image to the dialog
		Image image = new Image("dialog-error.png");;

		dialogContents.add(image);
		dialogContents.setCellHorizontalAlignment(image,HasHorizontalAlignment.ALIGN_CENTER);

		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("Ok",
				new ClickListener() {
			public void onClick(Widget sender) {
				dialogBox.hide();
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

		// Return the dialog box
		return dialogBox;
	}
	
	public static DialogBox createHtmlDialogBox(String title, String HtmlUrl) 
	{
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwHtmlDialogBox");
		dialogBox.setText("BikeBingle: " + title);

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		int h = Window.getClientHeight() - 80;
		int w = Window.getClientWidth() - 200;
		if (w < 400)
			w = 400;
		dialogBox.setWidth(Integer.toString(w) + "px");
		dialogBox.setHeight(Integer.toString(h) + "px");
		
		Frame f = new Frame(HtmlUrl);
		f.getElement().setAttribute("frameBorder","0");
		//f.getElement().setAttribute("scrolling","no");
		f.setStylePrimaryName("borderlessFrameForHelpPanel");
		f.setWidth(Integer.toString(w-10) + "px");
		f.setHeight(Integer.toString(h-30) + "px");
		
		
		dialogContents.add(f);
		dialogContents.setCellHorizontalAlignment(f, HasHorizontalAlignment.ALIGN_CENTER);

		// Add an image to the dialog
		//Image image = new Image("dialog-error.png");;
		//dialogContents.add(image);
		//dialogContents.setCellHorizontalAlignment(image,HasHorizontalAlignment.ALIGN_CENTER);

		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("Ok",
				new ClickListener() {
			public void onClick(Widget sender) {
				dialogBox.hide();
			}
		});
		closeButton.setWidth("100px");
		closeButton.setStylePrimaryName("addStackPanel-Button");
		
		dialogContents.add(closeButton);
		if (LocaleInfo.getCurrentLocale().isRTL()) {
			dialogContents.setCellHorizontalAlignment(closeButton,
					HasHorizontalAlignment.ALIGN_LEFT);

		} else {
			dialogContents.setCellHorizontalAlignment(closeButton,
					HasHorizontalAlignment.ALIGN_RIGHT);
		}

		// Return the dialog box
		return dialogBox;
	}

	
}
