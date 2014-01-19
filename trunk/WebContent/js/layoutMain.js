var menuStore;
var dishStore;
Ext.onReady(function() {

	var lanStore = new Ext.data.TreeStore({
		model : 'Item',
		root : {
			text : 'Root 1',
			expanded : true,
			children : [ {
				text : 'English(United States)',
				id : 'en_US',
				leaf : true
			}, {
				text : '中文(简体)',
				id : 'zh_CN',
				leaf : true
			}, {
				text : 'Svensk',
				id : 'sv_SE',
				leaf : true
			} ]
		}
	});

	// Go ahead and create the TreePanel now so that we can use it below
	var lanPanel = Ext.create('Ext.tree.Panel', {
		id : 'tree-panel',
		title : 'Languages',
		region : 'north',
		split : true,
		height : 150,
		minSize : 150,
		rootVisible : false,
		autoScroll : true,
		bodyStyle : 'padding-top:5px;',
		store : lanStore,
		listeners : {
			afterrender : function() {
				var record = lanPanel.getStore().getNodeById('en_US');
				this.getSelectionModel().select(record);
			}
		}
	});

	function reloadMenu() {
		menuStore.proxy.url = './loadXml?language=' + language + '&xml=MainMenu.xml';
		menuStore.removeAll();
		menuStore.load();
	}

	function reloadDish() {
		dishStore.proxy.url = './loadXml?language=' + language + '&xml=Dishes.xml';
		dishStore.removeAll();
		dishStore.load();
	}

	var mmStore = new Ext.data.TreeStore({
		model : 'Item',
		root : {
			text : 'Root 1',
			expanded : true,
			children : [ {
				text : 'Menus',
				id : 'menus',
				leaf : true
			}, {
				text : 'Dishes',
				id : 'dishes',
				leaf : true
			} ]
		}
	});

	var editPanel = Ext.create('Ext.tree.Panel', {
		id : 'edit-panel',
		title : 'Management',
		region : 'center',
		rootVisible : false,
		bodyStyle : 'padding-top:5px;',
		autoScroll : true,
		store : mmStore
	});

	// Assign the function to be called on tree node click.
	lanPanel.getSelectionModel().on('select', function(selModel, record) {
		if (record.get('leaf')) {
			language = record.getId();
			reloadMenu();
			var muItem = editPanel.getStore().getNodeById('menus');
			editPanel.getSelectionModel().select(muItem);
			// Ext.Msg.alert("Leaf clicked,Language="+language);
		}
	});

	// Assign the function to be called on tree node click.
	editPanel.getSelectionModel().on('select', function(selModel, record) {
		if (record.get('leaf')) {
			var id = record.getId();
			var p = Ext.getCmp('content-panel');
			var items = p.items;
			for ( var i = 0; i < items.length; i++) {
				p.remove(items[i]);
			}
			if (id == 'menus') {
				reloadMenu();
				p.add(menuGrid);
			} else if (id = 'dishes') {
				reloadDish();
				p.add(dishGrid);
			}
			p.doLayout();
		}
	});

	Ext.define('MainMenu', {
		extend : 'Ext.data.Model',
		proxy : {
			type : 'ajax',
			reader : 'xml'
		},
		fields : [ {
			name : 'id',
			mapping : '@id'
		}, {
			name : 'name',
			mapping : '@name'
		}  ]
	});

	// create the Data Store
	menuStore = Ext.create('Ext.data.Store', {
		model : 'MainMenu',
		// autoLoad: true,
		proxy : {
			// load using HTTP
			type : 'ajax',
			url : './loadXml?language=' + language + '&xml=MainMenu.xml',
			// the return will be XML, so lets set up a reader
			reader : {
				type : 'xml',
				// records will have an "Item" tag
				record : 'MenuItem',
				idProperty : 'id'
			// totalRecords: '@total'
			}
		}
	});

	// create the grid
	var menuGrid = Ext.create('Ext.grid.Panel', {
		id : 'grid-panel',
		title : 'Menu Management',
		region : 'center',
		store : menuStore,
		selModel : Ext.create('Ext.selection.CheckboxModel'),
		tbar : menuTools,
		columns : [ {
			text : "ID",
			dataIndex : 'id',
			width : 60
		}, {
			text : "Name",
			dataIndex : 'name',
			width : 350
		} ],
		// renderTo:'gridView',
		// width: 300,
		// height: 200,
		border : false
	});

	Ext.define('Dishes', {
		extend : 'Ext.data.Model',
		proxy : {
			type : 'ajax',
			reader : 'xml'
		},
		fields : [ {
			name : 'id',
			mapping : '@id'
		}, {
			name : 'dishNumber',
			mapping : '@dishNumber'
		}, {
			name : 'name',
			mapping : '@name'
		}, {
			name : 'image',
			mapping : '@image'
		}, {
			name : 'belongsTo',
			mapping : '@belongsTo'
		}, {
			name : 'recommended',
			mapping : '@recommended'
		}, {
			name : 'enabled',
			mapping : '@enabled'
		}, {
			name : 'file',
			mapping : '@file'
		}, {
			name : 'price',
			mapping : '@price'
		},{
			name : 'introduction',
			mapping: '/', 
			type: 'string'
		}]
	});

	// create the Data Store
	dishStore = Ext.create('Ext.data.Store', {
		model : 'Dishes',
		// autoLoad: true,
		proxy : {
			// load using HTTP
			type : 'ajax',
			url : './loadXml?language=' + language + '&xml=Dishes.xml',
			// the return will be XML, so lets set up a reader
			reader : {
				type : 'xml',
				// records will have an "Item" tag
				record : 'Dish',
				idProperty : 'id'
			// totalRecords: '@total'
			}
		}
	});

	// create the grid
	var dishGrid = Ext.create('Ext.grid.Panel', {
		id : 'dish-panel',
		title : 'Dish Management',
		region : 'center',		
		store : dishStore,
		selModel : Ext.create('Ext.selection.CheckboxModel'),
		tbar : dishTools,
		columns : [ {
			text : "ID",
			dataIndex : 'id',
			width : 60
		}, {
			text : "DishNumber",
			dataIndex : 'dishNumber',
			width : 80
		}, {
			text : "Name",
			dataIndex : 'name',
			width : 200
		},{
			text : "BelongsTo",
			dataIndex : 'belongsTo',
			renderer: belongsToRender,
			width : 350
		}, {
			text : "Recommended",
			dataIndex : 'recommended',
			width : 90
		}, {
			text : "Enabled",
			dataIndex : 'enabled',
			width : 70
		}, {
			text : "Price(kr)",
			dataIndex : 'price',
			align: 'right',
			width : 80
		} ],
		border : false
	});

    function belongsToRender(ids){
		var idList = ids.split(',');
		var menuNames='';
		for(var i=0;i<idList.length;i++){
			var mn = menuStore.getById(idList[i]).get('name');
			if( i==0 ){
				menuNames = mn
			}else{
				menuNames = menuNames+', '+mn;
			}
		}
		return menuNames;
	}

	var contentPanel = {
		id : 'content-panel',
		// title:'Content',
		region : 'center',
		layout : 'border',
		margins : '2 5 5 0',
		// renderTo:'content',
		border : true,
		items : [ menuGrid ]
	};

	// Finally, build the main layout once all the pieces are ready. This is
	// also a good
	// example of putting together a full-screen BorderLayout within a Viewport.
	Ext.create('Ext.Viewport', {
		layout : 'border',
		title : 'Ext Layout Browser',
		items : [ {
			xtype : 'box',
			id : 'header',
			region : 'north',
			html : '<h1>&nbsp;&nbsp;&nbsp;&nbsp;Menu Editor</h1>',
			height : 80
		}, {
			layout : 'border',
			id : 'layout-browser',
			region : 'west',
			border : false,
			split : true,
			margins : '2 0 5 5',
			width : 220,
			minSize : 100,
			maxSize : 500,
			items : [ lanPanel, editPanel ]
		}, contentPanel ],
		renderTo : Ext.getBody()
	});
});
