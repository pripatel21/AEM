
use(
["wcm/foundation/components/utils/AuthoringUtils.js"],
function (AuthoringUtils) {
var imgPos = granite.resource.properties["imagePosition"];
var textPos = (imgPos=='right')?'left':'right';


return {
    textPosition:textPos

};


});
