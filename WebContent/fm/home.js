var remotingProvider = Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
Ext.Loader.setConfig({
	enabled : true
});
Ext.tip.QuickTipManager.init();
// should be set request timeout for ajax call such as: form, load,...
Ext.Ajax.timeout = remotingProvider.timeout;
Ext.override(Ext.form.Basic, {     timeout: Ext.Ajax.timeout / 1000 });
Ext.override(Ext.data.proxy.Server, {     timeout: Ext.Ajax.timeout });
Ext.override(Ext.data.Connection, {     timeout: Ext.Ajax.timeout });
Ext.Loader.setPath('Ext.ux', 'ext-4.1.1/ux');
Ext.require([
	'Ext.ux.window.Notification',
	'Ext.ux.CheckColumn',
	'Ext.ux.form.MultiSelect',
	'Ext.ux.form.ItemSelector',
	'fm.store.Friends'
]);

Ext.direct.Manager.on('exception', function(e) {
	if (e.message == 'Session Timeout') {
		Ext.Msg.alert('Error', 'Session is timeout. Please login again',
				function(button) {
					window.location = 'j_spring_security_logout'; // TODO - using spring framework security
				});
	}
	else if (e.message == 'Data Access') {
		Ext.Msg.alert('Error', 'Issue with data persistence!!! Email has been sent to IT department.');
	}
	else if (e.message == 'Unknown') {
		Ext.Msg.alert('Error', 'Critical issue!!! Email has been sent to IT department.');
	}
});

var currentForm;
var activeComponent;
var application;
var reportWindow;
var myMask;
var PER_VISIT_TEXT = 'Per Visit';
Ext.application({
	name : 'fm',
	appFolder : 'fm',
	controllers : ['Friends'],

	launch : function() {
		application = this;
		application.getController('Navigation').control({
			'pageheader': {
				boxready: this.handlePermission
			}
		});
		
		application.getController('Dashboard').control({
			'dashboard': {
				boxready: this.handlePermission
			}
		});
		
		application.getController('Friends').control({
			'FiendsList': {
				boxready: this.handlePermission
			}
		});
		
		Ext.create('Ext.container.Viewport', {

			layout : {
				type : 'border'
			},
			items : [ {
				region : 'north',
				collapsible : false,
				border : false,
				height : 62,
				xtype : 'pageheader'
			}, {
				region : 'center',
				activeItem : 0,
				layout: 'card',
				id : 'content-panel',
				items : [
				    {
				    	xtype : 'dashboard'
				    }
				]
			}, {
				region : 'south',
				collapsible : false,
				border : false,
				height : 25,
				xtype : 'pagefooter'
			} ]
		});
		
	}	
});

