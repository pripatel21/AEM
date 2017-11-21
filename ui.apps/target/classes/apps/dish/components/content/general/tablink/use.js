
use(
["wcm/foundation/components/utils/AuthoringUtils.js"],
function (AuthoringUtils) {
var leftLinks = granite.resource.properties["leftLinks"];
var rightLinks = granite.resource.properties["rightLinks"];


return {
    leftLinks:getItemArray(leftLinks),
    rightLinks:getItemArray(rightLinks)
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
