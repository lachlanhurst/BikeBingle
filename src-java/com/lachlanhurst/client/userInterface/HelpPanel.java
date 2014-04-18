package com.lachlanhurst.client.userInterface;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.util.DialogGenerator;

/**
 * help panel will display links to, or make popups for the credits, FAQ,
 * blog, feeback, terms of use.
 * 
 * @author lachlan
 *
 */
public class HelpPanel extends VerticalPanel 
{
	
	
	protected UiStateManager _uiStateManager;

	protected int _width;
	protected int _height;
	
	protected Button _buttonIntroduction = null;
	protected Button _buttonFAQ = null;
	protected Button _buttonFeedback = null;
	protected Button _buttonTermsOfUse = null;
	protected Button _buttonCredits = null;
	
	
	public HelpPanel(UiStateManager uiStateManager, String width, int height)
	{
		_uiStateManager = uiStateManager;
		_width = Integer.parseInt(width.substring(0, width.length() - 2)) -5;
		_height = height;
		this.setWidth(Integer.toString(_width) + "px");
		
		initialise();
	}
	
	protected void initialise()
	{
		this.setSpacing(10);
		
		this.add(getButtonIntroduction());
		this.add(getButtonFAQ());
		this.add(getButtonFeedback());
		this.add(getButtonTermsOfUse());
		this.add(getButtonCredits());
	}
	
	protected Button getButtonIntroduction()
	{
		if (_buttonIntroduction == null)
		{
			_buttonIntroduction = new Button("Introduction");
			_buttonIntroduction.setWidth(Integer.toString(_width - 15));
			_buttonIntroduction.setStylePrimaryName("addStackPanel-Button");
			_buttonIntroduction.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					doIntroductionClick();
				}
			});
		}
		return _buttonIntroduction;
	}
	
	protected void doIntroductionClick()
	{
		DialogBox dialogBox = DialogGenerator.createHtmlDialogBox("Introduction","Introduction.html");
		dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        dialogBox.show();
	}
	
	protected Button getButtonFAQ()
	{
		if (_buttonFAQ == null)
		{
			_buttonFAQ = new Button("FAQ");
			_buttonFAQ.setWidth(Integer.toString(_width - 15));
			_buttonFAQ.setStylePrimaryName("addStackPanel-Button");
			_buttonFAQ.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					doFAQClick();
				}
			});
		}
		return _buttonFAQ;
	}
	
	protected void doFAQClick()
	{
		DialogBox dialogBox = DialogGenerator.createHtmlDialogBox("Frequenty Asked Questions (FAQ)","FAQ.html");
		dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        dialogBox.show();
	}
	
	protected Button getButtonFeedback()
	{
		if (_buttonFeedback == null)
		{
			_buttonFeedback = new Button("Feedback");
			_buttonFeedback.setWidth(Integer.toString(_width - 15));
			_buttonFeedback.setStylePrimaryName("addStackPanel-Button");
			_buttonFeedback.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					doFeedbackClick();
				}
			});
		}
		return _buttonFeedback;
	}
	
	protected void doFeedbackClick()
	{
		String url = "http://bikebingle.blogspot.com/2008/11/feedback.html";
		DialogBox dialogBox = DialogGenerator.createHtmlDialogBox("Feedback",url);
		dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        dialogBox.show();
		
	}
	
	protected Button getButtonTermsOfUse()
	{
		if (_buttonTermsOfUse == null)
		{
			_buttonTermsOfUse = new Button("Terms of Use");
			_buttonTermsOfUse.setWidth(Integer.toString(_width - 15));
			_buttonTermsOfUse.setStylePrimaryName("addStackPanel-Button");
			_buttonTermsOfUse.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					doTermsOfUseClick();
				}
			});
		}
		return _buttonTermsOfUse;
	}
	
	protected void doTermsOfUseClick()
	{
		DialogBox dialogBox = DialogGenerator.createHtmlDialogBox("Terms of Use","TermsOfUse.html");
		dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        dialogBox.show();
	}
	
	protected Button getButtonCredits()
	{
		if (_buttonCredits == null)
		{
			_buttonCredits = new Button("Credits");
			_buttonCredits.setWidth(Integer.toString(_width - 15));
			_buttonCredits.setStylePrimaryName("addStackPanel-Button");
			_buttonCredits.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					doCreditsClick();
				}
			});
		}
		return _buttonCredits;
	}
	
	protected void doCreditsClick()
	{
		DialogBox dialogBox = DialogGenerator.createHtmlDialogBox("Credits","Credits.html");
		dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        dialogBox.show();
	}
}
