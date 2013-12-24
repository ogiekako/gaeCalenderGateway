'use strict';

describe('Service: Rawmailin', function () {

  // load the service's module
  beforeEach(module('frontendApp'));

  // instantiate service
  var Rawmailin;
  beforeEach(inject(function(_Rawmailin_) {
    Rawmailin = _Rawmailin_;
  }));

  it('should do something', function () {
    expect(!!Rawmailin).toBe(true);
  });

});
