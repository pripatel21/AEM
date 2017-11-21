
use(
["wcm/foundation/components/utils/AuthoringUtils.js"],
function (AuthoringUtils) {
var lists = granite.resource.properties["lists"];



return {
    lists:getItemArray(lists)
};

    function getItemArray(list){
        var array = [];
        if(list != null) {
			if( list.getClass().isArray() ) {
				for (var i = 0, l = list.length; i < l; i++) {
					var temp = JSON.parse(list[i]);
					array.push(temp);

			 	}
			}

		}
        return array;
    }

}


);
