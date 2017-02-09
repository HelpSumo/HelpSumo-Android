# HelpSumo

## Prerequisites :

* The library requires Android API Level 16+

## Installation 

	* Download HelpSumo.zip file and Extract it 
	* Import New Library Module and set required path to the one extracted

## Usage

Add a dependency to your build.gradle:

	dependencies {
    	 compile project(':HelpSumoSDK')
	}
	
Initialize Config:

	import com.helpsumo.api.ticketing.ticket.Helpsumo;
	import com.helpsumo.api.ticketing.ticket.HelpsumoConfig;

 	HelpsumoConfig hConfig; // include it main class

Set API Key

   	hConfig = new HelpsumoConfig("<YOUR-APP-KEY>");

Initialize setup:
	
	Helpsumo.getInstance(getApplicationContext()).init(hsConfig); 
	

To Add Ticket module

  	myTicketButton.setOnClickListener(new OnClickListener() { 
		@Override 
		public void onClick(View view) {
		Helpsumo.showTickets(getApplicationContext(),hConfig.getAppKey()); 
			}
		});

To Add Faq module

  	myFAQButton.setOnClickListener(new OnClickListener() {
	@Override 
		public void onClick(View view) { 
		Helpsumo.showFAQs(getApplicationContext(),hConfig.getAppKey()); 
			} 
		}); 

To Add Article module

	myKnowledgeBaseButton.setOnClickListener(new OnClickListener() {
	@Override 
		public void onClick(View view) { 
		Helpsumo.showArticles(getApplicationContext(),hConfig.getAppKey()); 
			} 
		});

## Author

Help Sumo

## License

HelpSumoSDK is available under the MIT license. See the LICENSE file for more info.
