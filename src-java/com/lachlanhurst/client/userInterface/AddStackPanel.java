package com.lachlanhurst.client.userInterface;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.widgetideas.client.SliderBar.LabelFormatter;
import com.google.gwt.widgetideas.datepicker.client.DatePicker;

import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationException;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationManager;
import com.lachlanhurst.client.userInterface.MapManager.StackLocationPickedListener;
import com.lachlanhurst.client.util.DialogGenerator;
import com.lachlanhurst.client.util.RandomBikeStackGenerator;

/**
 * the panel that allows a user to add stack data to the database
 * @author lachlan
 *
 */
public class AddStackPanel extends VerticalPanel implements MapManager.StackLocationPickedListener
{
	protected UiStateManager _uiStateManager;
	protected MapManager _mapManager;
	
	//the gui components
	protected Label _labelUser = null;
	protected Label _labelStackType = null;
	
	protected ListBox _listBoxStackTypes = null;
	protected Label _labelStackTypeDescription = null;
	
	protected DecoratorPanel _panelAccidentType = null;
	protected SimplePanel _panelAccidentTypeDisplay = null;
	
	protected DecoratorPanel _panelLocation = null;
	protected Label _labelLocationNotification = null;
	
	private int _width;
	
	protected DecoratorPanel _panelDate = null;
	protected DatePicker _datePickerOccured = null;
	
	protected DecoratorPanel _panelInjurySeverty = null;
	protected SliderBar _sliderBarInjurySeverity = null;
	protected Label _labelInjurySeverityDescription = null;
	
	protected DecoratorPanel _panelStackDescription = null;
	protected TextArea _textAreaDescription = null;
	
	protected DecoratorPanel _panelLink = null;
	protected TextArea _textAreaLink = null;
	
	protected SimplePanel _panelNextFinish = null;
	protected PushButton _buttonNext = null;
	protected PushButton _buttonPrev = null;
	protected PushButton _buttonFinish = null;
	
	protected DecoratorPanel _panelLogin = null;
	protected Button _buttonLogin = null;
	
	protected String _validationError = null;
	protected BikeStack _lastStack = null;
	
	protected BikeStackTypes _bingleTypeManager = null;
	
	protected BinglesCommunicationManager.AddBingleAttemptListener _addListener = null;
	
	protected Button _buttonGenerateRandom = null;
	
	public AddStackPanel(UiStateManager uiStateManager, MapManager mapManager, String width, BikeStackTypes bingleTypeManager)
	{
		_bingleTypeManager = bingleTypeManager;
		_uiStateManager = uiStateManager;
		_mapManager = mapManager;
		_width = Integer.parseInt(width.substring(0, width.length() - 2)) + 7;
		this.setWidth(width);
		
		initialise();
		_mapManager.addStackLocationPickedListener(this);
				
		_addListener = new BinglesCommunicationManager.AddBingleAttemptListener()
		{
			public void onAddBingleAttempt(boolean success, String message) 
			{
				if (!success)
				{
					final DialogBox dialogBox = createAddStackDialogBox(false,message);
				    dialogBox.setAnimationEnabled(true);
		            dialogBox.center();
		            dialogBox.show();
				}
				else
				{
					//now clear the user interface
					resetUserInterface();
					fireStackAddedPickedEvent(_lastStack);
				}
				
			}
		};
		_mapManager.getBinglesCommunicator().addAddBingleAttemptListener(_addListener);
		
	}
	
	protected void initialise()
	{
		this.setSpacing(1);
		this.add(getUserLabel());
		
		//the first page items
		this.add(getPanelAccidentType());
		this.add(getPanelLocation());
		this.add(getPanelDate());
		
		//the items on the second page
		this.add(getPanelInjurySeverty());
		this.add(getPanelStackDescription());
		this.add(getPanelLink());
		
		//the login panel, only gets shown if user is not logged in
		this.add(getButtonLogin());
		
		this.add(getPanelNextFinish());
		//this.add(getRandomGenerationButton());
		
		this.setVisibleStatePageOne(true);
		this.setVisibleStatePageTwo(false);
		
		_uiStateManager.addUserLoginListener(new UiStateManager.UserLoginListener()
		{
			public void onUserLogin(String name, String email, boolean isSuccess) 
			{
				if (isSuccess)
				{
					setUserIsLoggedIn();
				}
				else
				{
					Exception ex = new Exception("Error contacting BikeBingle Server, please retry login");
					handleException(ex);
				}
				
			}
		});
		
		if (!_uiStateManager.isUserLoggedIn())
		{
			getPanelNextFinish().setVisible(false);
			this.setVisibleStatePageOne(false);
			this.setVisibleStatePageTwo(false);
			this.getButtonLogin().setVisible(true);
		}
		
	}
	
	private void handleException(Exception ex)
	{
		final DialogBox dialogBox = DialogGenerator.createExceptionDialogBox(ex);
	    dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        dialogBox.show();
	}
	
	protected Label getUserLabel()
	{
		if (_labelUser == null)
		{
			_labelUser = new Label();
			
			if (_uiStateManager.isUserLoggedIn())
			{
				_labelUser.setText("Welcome " + _uiStateManager.getPreferredUserName());
			}
			else
			{
				_labelUser.setText("You must be logged in to add data");
			}
		}
		return _labelUser;
	}
	
	protected DecoratorPanel getPanelAccidentType()
	{
		if (_panelAccidentType == null)
		{
			_panelAccidentType = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setSpacing(1);
			vp.add(getLabelStackType());
			vp.add(getListBoxStackTypes());
			HorizontalPanel hp = new HorizontalPanel();
			hp.setSpacing(3);
			hp.add(getPanelAccidentTypeImage());
			hp.add(getLabelStackTypeDescription());
			
			vp.add(hp);
			vp.setStylePrimaryName("addStackPanel-InnerPanel");
			//_panelAccidentType.setStylePrimaryName("addStackPanel-DecoratorPanel");
			vp.setWidth(_width + "px");
			_panelAccidentType.add(vp);
		}
		return _panelAccidentType;
	}
	
	protected SimplePanel getPanelAccidentTypeImage()
	{
		if (_panelAccidentTypeDisplay == null)
		{
			_panelAccidentTypeDisplay = new SimplePanel();
			_panelAccidentTypeDisplay.add(_bingleTypeManager.getImageById(_bingleTypeManager.getIdFromIndex(0)));
			_panelAccidentTypeDisplay.setWidth("60px");
			_panelAccidentTypeDisplay.setHeight("60px");
		}
		return _panelAccidentTypeDisplay;
	}
	
	protected Label getLabelStackType()
	{
		if (_labelStackType == null)
		{
			_labelStackType = new Label("Accident type");
			_labelStackType.setStylePrimaryName("addStackPanel-SubHeading");
		}
		return _labelStackType;
	}
	
	protected ListBox getListBoxStackTypes()
	{
		if (_listBoxStackTypes == null)
		{
			_listBoxStackTypes = new ListBox(false);
			_listBoxStackTypes.setWidth((_width-10) + "px");
			
			Iterator names = _bingleTypeManager.getListOfTypeNames().iterator();
			while (names.hasNext())
			{
				String name = (String)names.next();
				_listBoxStackTypes.addItem(name);
			}
			
			//need to set the description when an stack typeis selected
			_listBoxStackTypes.addChangeListener(new ChangeListener()
			{
				public void onChange(Widget sender) 
				{
					int selectedIndex = getListBoxStackTypes().getSelectedIndex();
					int typeId = _bingleTypeManager.getIdFromIndex(selectedIndex);
					String desc = _bingleTypeManager.getTypeDescriptionById(typeId);
					getLabelStackTypeDescription().setText(desc);
					getPanelAccidentTypeImage().clear();
					getPanelAccidentTypeImage().add(_bingleTypeManager.getImageById(typeId));
				}
			});
			
			//shouldn't need this but firefox wasn;t listening to the above event
			_listBoxStackTypes.addKeyboardListener(new KeyboardListener(){
				public void onKeyDown(Widget sender, char keyCode, int modifiers) {}

				public void onKeyPress(Widget sender, char keyCode,int modifiers) {}

				public void onKeyUp(Widget sender, char keyCode, int modifiers) 
				{
					int selectedIndex = getListBoxStackTypes().getSelectedIndex();
					int typeId = _bingleTypeManager.getIdFromIndex(selectedIndex);
					String desc = _bingleTypeManager.getTypeDescriptionById(typeId);
					getLabelStackTypeDescription().setText(desc);
					getPanelAccidentTypeImage().clear();
					getPanelAccidentTypeImage().add(_bingleTypeManager.getImageById(typeId));
				}
				
			});
		}
		return _listBoxStackTypes;
	}
	
	protected Label getLabelStackTypeDescription()
	{
		if (_labelStackTypeDescription == null)
		{
			_labelStackTypeDescription = new Label();
			_labelStackTypeDescription.setHeight("80px");
			_labelStackTypeDescription.setStylePrimaryName("addStackPanel-TypeDescription");
			_labelStackTypeDescription.setText(_bingleTypeManager.getTypeDescriptionById(_bingleTypeManager.getIdFromIndex(0)));
		}
		return _labelStackTypeDescription;
	}
	
	protected DecoratorPanel getPanelLocation()
	{
		if (_panelLocation == null)
		{
			_panelLocation = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setStylePrimaryName("addStackPanel-InnerPanel");
			Label locationLabel = new Label("Location");
			locationLabel.setStylePrimaryName("addStackPanel-SubHeading");

			vp.add(locationLabel);
			vp.add(getLabelLocationNotification());
			vp.setWidth(_width + "px");
			_panelLocation.add(vp);
		}
		return _panelLocation;
	}
	
	protected Label getLabelLocationNotification()
	{
		if (_labelLocationNotification == null)
		{
			_labelLocationNotification = new Label();
			if (_mapManager.isPointPicked())
			{
				_labelLocationNotification.setText("Location picked");
				_labelLocationNotification.setStylePrimaryName("addStackPanel-ValidationLabelGood");
			}
			else
			{
				_labelLocationNotification.setText("No location selected");
				_labelLocationNotification.setStylePrimaryName("addStackPanel-ValidationLabelBad");
			}
			
		}
		return _labelLocationNotification;
	}

	public void onStackLocationPicked(LatLng pickedLocation) 
	{
		if (pickedLocation == null)
		{
			_labelLocationNotification.setText("No location selected");
			_labelLocationNotification.setStylePrimaryName("addStackPanel-ValidationLabelBad");
		}
		else
		{
			_labelLocationNotification.setText("Location picked");
			_labelLocationNotification.setStylePrimaryName("addStackPanel-ValidationLabelGood");
		}
		
	}
	
	public DecoratorPanel getPanelDate()
	{
		if (_panelDate == null)
		{
			_panelDate = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setStylePrimaryName("addStackPanel-InnerPanel");
			Label locationLabel = new Label("Date of stack");
			locationLabel.setStylePrimaryName("addStackPanel-SubHeading");
			vp.add(locationLabel);
			vp.setWidth(_width + "px");
			vp.add(getDatePickerOccured());
			_panelDate.add(vp);
			
			
		}
		return _panelDate;
	}
	
	protected DatePicker getDatePickerOccured()
	{
		if (_datePickerOccured == null)
		{
			_datePickerOccured = new DatePicker();
			_datePickerOccured.setWidth(_width-10 + "px");
			//_datePickerOccured.setStyleName("gwt-DatePicker");
			_datePickerOccured.setStyleName("gwt-DatePicker");
			
			//Date today = new Date();
			//_datePickerOccured.setFullDate(today);
			
		}
		return _datePickerOccured;
	}
	
	protected void setVisibleStatePageOne(boolean visible)
	{
		getPanelAccidentType().setVisible(visible);
		getPanelLocation().setVisible(visible);
		getPanelDate().setVisible(visible);
		
	}
	
	protected void setVisibleStatePageTwo(boolean visible)
	{
		getPanelInjurySeverty().setVisible(visible);
		getPanelStackDescription().setVisible(visible);
		getButtonFinish().setVisible(visible);
		getPanelLink().setVisible(visible);
	}
	
	protected DecoratorPanel getPanelInjurySeverty()
	{
		if (_panelInjurySeverty == null)
		{
			_panelInjurySeverty = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setStylePrimaryName("addStackPanel-InnerPanel");
			Label injuryLabel = new Label("Injury Severity");
			injuryLabel.setStylePrimaryName("addStackPanel-SubHeading");

			vp.add(injuryLabel);
			vp.add(getSliderBarInjurySeverity());
			vp.add(getLabelInjurySeverityDescription());
			vp.setWidth(_width + "px");
			_panelInjurySeverty.add(vp);
		}
		return _panelInjurySeverty;
	}
	
	protected SliderBar getSliderBarInjurySeverity()
	{
		if (_sliderBarInjurySeverity == null)
		{
			LabelFormatter lf = new LabelFormatter(){

				public String formatLabel(SliderBar slider, double value) 
				{
					return Integer.toString((int)value);
				}
				
			};
			_sliderBarInjurySeverity = new SliderBar(0,10,lf);
			_sliderBarInjurySeverity.setStepSize(1.0);
			_sliderBarInjurySeverity.setCurrentValue(5.0);
			_sliderBarInjurySeverity.setNumTicks(10);
			_sliderBarInjurySeverity.setNumLabels(5);
			
			_sliderBarInjurySeverity.setWidth(_width-10 + "px");
			_sliderBarInjurySeverity.setHeight("50px");
			_sliderBarInjurySeverity.addChangeListener(new ChangeListener()
			{
				public void onChange(Widget sender) 
				{
					SliderBar slider = (SliderBar)sender;
					String severity = getInjurySeverityText(slider.getCurrentValue());
					getLabelInjurySeverityDescription().setText(severity);
				}
				
			});
		}
		return _sliderBarInjurySeverity;
	}
	
	public String getInjurySeverityText(double selectedValue)
	{
		if (selectedValue == 0.0)
		{
			return "Only pride";
		}
		else if (selectedValue == 1.0)
		{
			return "Slight grazing to one area";
		}
		else if (selectedValue == 2.0)
		{
			return "Slight grazing to one area";
		}
		else if (selectedValue == 3.0)
		{
			return "Slight grazing to two or more areas";
		}
		else if (selectedValue == 4.0)
		{
			return "Slight grazing to two or more areas";
		}
		else if (selectedValue == 5.0)
		{
			return "Slight grazing to two or more areas";
		}
		else if (selectedValue == 6)
		{
			return "Broken bone/ single fracture";
		}
		else if (selectedValue == 7)
		{
			return "Broken bone/ single fracture";
		}
		else if (selectedValue == 8)
		{
			return "Multiple fractures";
		}
		else if (selectedValue == 9)
		{
			return "Multiple fractures";
		}
		else if (selectedValue == 10)
		{
			return "Fatal";
		}
		else
		{
			return "";
		}
	}
	
	protected Label getLabelInjurySeverityDescription()
	{
		if (_labelInjurySeverityDescription == null)
		{
			_labelInjurySeverityDescription = new Label();
			_labelInjurySeverityDescription.setHeight("40px");
			_labelInjurySeverityDescription.setStylePrimaryName("addStackPanel-TypeDescription");
			_labelInjurySeverityDescription.setText(getInjurySeverityText(5.0));
		}
		return _labelInjurySeverityDescription;
	}
	
	protected DecoratorPanel getPanelStackDescription()
	{
		if (_panelStackDescription == null)
		{
			_panelStackDescription = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setStylePrimaryName("addStackPanel-InnerPanel");
			Label descriptionLabel = new Label("Description");
			descriptionLabel.setStylePrimaryName("addStackPanel-SubHeading");

			vp.add(descriptionLabel);
			vp.add(getTextAreaDescription());
			vp.setWidth(_width + "px");
			_panelStackDescription.add(vp);
		}
		return _panelStackDescription;
	}
	
	public static final String DEFAULT_DESCRIPTION = "Enter optional description of accident.  May also include further details of injuries to person and bike.";
	
	protected TextArea getTextAreaDescription()
	{
		if (_textAreaDescription == null)
		{
			_textAreaDescription = new TextArea();
			_textAreaDescription.setText(DEFAULT_DESCRIPTION);
			_textAreaDescription.setStylePrimaryName("addStackPanel-TypeDescription");
			_textAreaDescription.setWidth(_width-10 + "px");
			_textAreaDescription.setHeight(140 + "px");
			_textAreaDescription.setStylePrimaryName("addStackPanel-DescriptionTextBox");
			_textAreaDescription.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					TextArea ta = (TextArea)sender;
					if (ta.getText().compareTo(DEFAULT_DESCRIPTION) == 0)
					{
						ta.setText("");
					}
				}
			});
		}
		return _textAreaDescription;
	}
	
	protected String sanitiseString(String txt)
	{
		txt = txt.replace('<', '_');
		txt = txt.replace('>', '_');
		return txt;
	}
	
	protected String getStackDescription()
	{
		if (getTextAreaDescription().getText().compareTo(DEFAULT_DESCRIPTION) == 0)
		{
			return "";
		}
		else
		{
			String txt = getTextAreaDescription().getText();
			txt = sanitiseString(txt);
			return txt;
		}
	}
	
	protected DecoratorPanel getPanelLink()
	{
		if (_panelLink == null)
		{
			_panelLink = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setSpacing(2);
			vp.setStylePrimaryName("addStackPanel-InnerPanel");
			Label urlLabel = new Label("Related Link (URL)");
			urlLabel.setStylePrimaryName("addStackPanel-SubHeading");

			vp.add(urlLabel);
			vp.add(getTextAreaLink());
			vp.setWidth(_width + "px");
			_panelLink.add(vp);
			_panelLink.setVisible(false);
		}
		return _panelLink;
	}
	
	public static final String DEFAULT_URL = "Enter optional URL";
	protected TextArea getTextAreaLink()
	{
		if (_textAreaLink == null)
		{
			_textAreaLink = new TextArea();
			_textAreaLink.setText(DEFAULT_URL);
			_textAreaLink.setStylePrimaryName("addStackPanel-Link");
			_textAreaLink.setWidth(_width-10 + "px");
			_textAreaLink.setVisibleLines(1);
			_textAreaLink.setTitle("Optional URL for page relating to accident details");
			_textAreaLink.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					TextArea ta = (TextArea)sender;
					if (ta.getText().compareTo(DEFAULT_URL) == 0)
					{
						ta.setText("");
					}
				}
			});
		}
		return _textAreaLink;
	}
	
	protected String getLink()
	{
		if (getTextAreaLink().getText().compareTo(DEFAULT_URL) == 0)
		{
			return "";
		}
		else
		{
			String txt = getTextAreaLink().getText();
			txt = sanitiseString(txt);
			return txt;
		}
	}
	
	/**
	 * checks if the user entered data is correct
	 * @return
	 */
	protected boolean isValidAddStackData()
	{
		if (!_mapManager.isPointPicked())
		{
			_validationError = "No location specified, please pick accident location on map window";
			return false;
		}
		
		Date selected = getDatePickerOccured().getSelectedDate();
		if (selected == null)
		{
			_validationError = "No date specified, please select date accident occured";
			GWT.log("no date selected", null);
			return false;
		}
		
		
		return true;
	}
	
	/**
	 * gets a new bikestack object from the user interface
	 * @return
	 */
	protected BikeStack getBikeStackFromUserInterface()
	{
		BikeStack bs = new BikeStack();
		bs.setUser(_uiStateManager.getUserName());
		LatLng pickedPoint = _mapManager.getPickedLocation();
		bs.setLocation(pickedPoint.getLatitude(), pickedPoint.getLongitude());
		bs.setOccuredDate(getDatePickerOccured().getSelectedDate());
		int selIndex = getListBoxStackTypes().getSelectedIndex();
		int type = _bingleTypeManager.getIdFromIndex(selIndex);
		bs.setType(type);
		bs.setDescription(getStackDescription());
		bs.setEntryDate(new Date());
		bs.setLink(getLink());
		bs.setInjurySeverity((int)getSliderBarInjurySeverity().getCurrentValue());
		
		return bs;
	}
	
	/**
	 * resets the add stack interface to near its deafult configuration
	 */
	protected void resetUserInterface()
	{
		getDatePickerOccured().setSelectedDate(getDatePickerOccured().getSelectedDate());
		//getListBoxStackTypes().setItemSelected(0, true);
		getSliderBarInjurySeverity().setCurrentValue(5.0);
		getTextAreaDescription().setText(DEFAULT_DESCRIPTION);
		setVisibleStatePageOne(true);
		setVisibleStatePageTwo(false);
		getButtonPrev().setVisible(false);
		getButtonNext().setVisible(true);
		_mapManager.setPickedLocation(null);
	}
	
	protected void doAddStack()
	{
		if (isValidAddStackData())
		{
			//then the user has provided valid data
			
            BikeStack newStack = getBikeStackFromUserInterface();
            
            try {
            	_lastStack = newStack;
				_mapManager.getBinglesCommunicator().requestAddBingle(newStack);
				

				
				//fireStackAddedPickedEvent(newStack);
			} catch (BinglesCommunicationException e) {
				handleException(e);
			}
		}
		else
		{
			//then some data is still required
			if (_validationError == null)
				_validationError = "Accident could not be added to database";
			final DialogBox dialogBox = createAddStackDialogBox(false,_validationError);
		    dialogBox.setAnimationEnabled(true);
            dialogBox.center();
            dialogBox.show();
		}
	}
	
	private DialogBox createAddStackDialogBox(boolean isValid, String message) 
	{
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwDialogBox");
		dialogBox.setText("BikeBingle");

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		// Add some text to the top of the dialog
		HTML details;
		if (isValid)
		{
			details = new HTML("Stack added to database");
		}
		else
		{
			details = new HTML("Stack not added to database </br>" + 
					           message + "</br>" +
					           "</br>" +
					           "Please retry in a few moments");
		}

		dialogContents.add(details);
		dialogContents.setCellHorizontalAlignment(details, HasHorizontalAlignment.ALIGN_CENTER);

		// Add an image to the dialog
		Image image;
		if (isValid)
		{
			image = new Image("dialog-information.png");
		}
		else
		{
			image = new Image("dialog-error.png");
		}
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



	public SimplePanel getPanelNextFinish()
	{
		if (_panelNextFinish == null)
		{
			_panelNextFinish = new SimplePanel();
			HorizontalPanel hp = new HorizontalPanel();
			//hp.setStylePrimaryName("addStackPanel-InnerPanel");
			//hp.setWidth(_width + "px");
			hp.setSpacing(1);
			
			hp.add(getButtonNext());
			hp.add(getButtonPrev());
			hp.add(getButtonFinish());
			_panelNextFinish.add(hp);
			
			
			
		}
		return _panelNextFinish;
	}
	
	protected PushButton getButtonNext()
	{
		
		if (_buttonNext == null)
		{
			_buttonNext = new PushButton(new Image("go-next.png"));
			_buttonNext.setWidth("50px");
			_buttonNext.setHeight("32px");
			_buttonNext.setStylePrimaryName("addStackPanel-Button");
			_buttonNext.addClickListener(new ClickListener()
			{

				public void onClick(Widget sender) 
				{
					//PushButton clicked = (PushButton)sender;
					setVisibleStatePageOne(false);
					setVisibleStatePageTwo(true);
					//getButtonFinish().setEnabled(true);
					getButtonNext().setVisible(false);
					getButtonPrev().setVisible(true);
				}
			});

		}
		return _buttonNext;
	}
	
	protected PushButton getButtonPrev()
	{
		
		if (_buttonPrev == null)
		{
			_buttonPrev = new PushButton(new Image("go-previous.png"));
			_buttonPrev.setWidth("50px");
			_buttonPrev.setHeight("32px");
			_buttonPrev.setStylePrimaryName("addStackPanel-Button");
			_buttonPrev.setVisible(false);
			_buttonPrev.addClickListener(new ClickListener()
			{

				public void onClick(Widget sender) 
				{
					//PushButton clicked = (PushButton)sender;
					setVisibleStatePageOne(true);
					setVisibleStatePageTwo(false);
					//getButtonFinish().setEnabled(false);
					getButtonPrev().setVisible(false);
					getButtonNext().setVisible(true);
				}
			});

		}
		return _buttonPrev;
	}
	
	protected PushButton getButtonFinish()
	{
		if (_buttonFinish == null)
		{
			_buttonFinish = new PushButton("Finish");
			_buttonFinish.setWidth(Integer.toString((_width /2) -5) + "px");
			_buttonFinish.setHeight("32px");
			_buttonFinish.setVisible(false);
			_buttonFinish.setStylePrimaryName("addStackPanel-Button");
			
			_buttonFinish.addClickListener(new ClickListener()
			{

				public void onClick(Widget sender) 
				{
					doAddStack();
				}
				
			});
		}
		return _buttonFinish;
	}
	
	protected Button getButtonLogin()
	{
		if (_buttonLogin == null)
		{
			_buttonLogin = new Button("Click to login");
			_buttonLogin.setStylePrimaryName("addStackPanel-Button");
			_buttonLogin.setWidth(_width + "px");
			_buttonLogin.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					try 
					{
						_uiStateManager.doUserLogin("add");
					} catch (UiStateManagerException e) {
						handleException(e);
					}
				}
				
			});
		}
		return _buttonLogin;
	}
	
	/**
	 * sets the user interface to the state that is to be displayed when a user is logged in.
	 */
	protected void setUserIsLoggedIn()
	{
		if (_uiStateManager.isUserLoggedIn())
		{
			setVisibleStatePageOne(true);
			setVisibleStatePageTwo(false);
			getPanelNextFinish().setVisible(true);
			getButtonLogin().setVisible(false);
			getUserLabel().setText("Welcome " + _uiStateManager.getPreferredUserName());
		}
		
	}
	
	protected Button getRandomGenerationButton()
	{
		if (_buttonGenerateRandom == null)
		{
			_buttonGenerateRandom = new Button("make random");
			_buttonGenerateRandom.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) {
					doRandomGeneration();
				}
				
			});
		}
		return _buttonGenerateRandom;
	}
	
	protected void doRandomGeneration()
	{
		LatLngBounds b = _mapManager._map.getBounds();
		Iterator stacks = RandomBikeStackGenerator.getLineOfStacks(100, b).iterator();
		
		_mapManager.getBinglesCommunicator().removeAddBingleAttemptListener(_addListener);
		
		while (stacks.hasNext())
		{
			BikeStack aStack = (BikeStack)stacks.next();
			try {
				_mapManager.getBinglesCommunicator().requestAddBingle(aStack);
			} catch (BinglesCommunicationException e) {
				handleException(e);
			}
		}
		
		_mapManager.getBinglesCommunicator().addAddBingleAttemptListener(_addListener);
		
	}
	
	
	//EVENT CODE
	private List _eventListenersStackAdded    = new ArrayList();
	
	public interface StackAddedListener extends java.util.EventListener
	{
	    void onStackAdded(BikeStack addedStack);
	}
	
	public void addStackAddedListener(StackAddedListener listener)
    {
		_eventListenersStackAdded.add(listener);
    }

    public void removeStackAddedListener(StackAddedListener listener)
    {
    	_eventListenersStackAdded.remove(listener);
    }

    protected void fireStackAddedPickedEvent(BikeStack stack)
    {
    	for(Iterator it = _eventListenersStackAdded.iterator(); it.hasNext();)
        {
    		StackAddedListener listener = (StackAddedListener) it.next();
            listener.onStackAdded(stack);
        }

    }

}
