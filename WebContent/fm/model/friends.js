Ext.define('fm.model.emails', {
	extend: 'Ext.data.Model',
	fields: [ 
	        'id',
			'emails',
		    ],
		     data: [
		    	[1, 'andy@example.com'],
		    	[2, 'john@example.com'],
		    	],
	proxy: {
		type: 'direct',
		api: {
			read: emailService.load,
			destroy: emailService.destroy
		},
		reader: {
			root: 'records'
		}
	}
});