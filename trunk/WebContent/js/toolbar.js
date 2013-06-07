var menuTools;
var dishTools;
Ext.onReady(function(){
  menuTools = new Ext.Toolbar({ 
        items:[ 
                { 
                    id:'btnAddMainMenu',
					text:'Add',
					xtype : 'button',
					iconCls : 'icon-add',
                    handler: function(){ addNewMenu();} 
                },
				'-',
				{ 
                    id:'btnRemoveMainMenu',
					text:'Remove',
					xtype : 'button',
					iconCls : 'icon-delete',
                    handler: function(){ removeMenus();} 
                },
				'-',
				{ 
                    id:'btnEditMenu',
					text:'Edit',
					xtype : 'button',
					iconCls : 'icon-edit',
                    handler: function(){ editMenu();} 
                }
            ] 
        });
  dishTools = new Ext.Toolbar({ 
        items:[ 
                { 
                    id:'btnAddDish',
					text:'Add',
					xtype : 'button',
					iconCls : 'icon-add',
                    handler: function(){ addNewDish();} 
                },
				'-',
				{ 
                    id:'btnRemoveDish',
					text:'Remove',
					xtype : 'button',
					iconCls : 'icon-delete',
                    handler: function(){ removeDishes();} 
                },
				'-',
				{ 
                    id:'btnEditDish',
					text:'Edit',
					xtype : 'button',
					iconCls : 'icon-edit',
                    handler: function(){ editDish();} 
                }
            ] 
        });
});


