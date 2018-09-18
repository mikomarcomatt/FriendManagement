Ext.direct.Manager.addProvider(Ext.app.REMOTING_API);
Ext.Loader.setConfig({
	enabled : true
});
Ext.tip.QuickTipManager.init();
Ext.Loader.setPath('Ext.ux', 'ext-4.1.1/ux');
Ext.require([
	'Ext.ux.window.Notification',
	'Ext.ux.CheckColumn'
]);

Ext.direct.Manager.on('exception', function(e) {
	Ext.Msg.alert('Error', e.message);
});

var currentForm;
var activeComponent;
var componentMap = new Ext.util.HashMap();

Ext.application({
	name : 'fm',
	appFolder : 'fm',
	controllers : ['Friends'],

	launch : function() {
		application = this;
		Ext.create('Ext.container.Viewport', {
			layout : {
				type : 'border',
				padding : 5
			},
			items : [	         
					{
						region : 'west',
						collapsible : true,
						title : 'Friends List',
						split : true,
						width : 250,
						minWidth : 245,
						layout: 'fit',
						items: {
							xtype: 'navigation'
						}
					}, 
					{
						region: 'center',
						layout: 'fit',
						id: 'content-panel'
						
					},
					{
						region: 'south',
			            collapsible: false,
			            split: false,
			            height: 25,
			            minHeight: 25,
			            xtype: 'navigationfooter'
					} 
			]
		});
	}
});
