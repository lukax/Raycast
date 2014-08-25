var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

mongoose.set('debug', true);

var BearSchema   = new Schema({
	author: String,
	message: String,
	time: Number,
	location: {type: Array, index: '2dsphere'},
	ip: String
});

BearSchema.static('findByAuthor', function (author, callback) {
	return this.find({ author: author }, callback);
});

BearSchema.static('findByRadius', function (radius, latitude, longitude, callback) {
	return this.find({ location: 
		{ $geoWithin: { $centerSphere: [ [ longitude, latitude ] , radius / 6371000  ] } } }, callback);
});

module.exports = mongoose.model('Messages', BearSchema);