'use strict';

describe('Service: Utils', function () {

  // load the service's module
  beforeEach(module('frontendApp'));

  // instantiate service
  var Utils;
  beforeEach(inject(function (_Utils_) {
    Utils = _Utils_;
  }));

  it('should detect empty strings', function () {
    expect(Utils.emptyStr()).toBeTruthy();
    expect(Utils.emptyStr(null)).toBeTruthy();
    expect(Utils.emptyStr('')).toBeTruthy();

    expect(Utils.emptyStr('test')).toBeFalsy();
    expect(Utils.emptyStr(' ')).toBeFalsy();
    expect(Utils.emptyStr('1')).toBeFalsy();
    expect(Utils.emptyStr(42)).toBeFalsy();
    expect(Utils.emptyStr({})).toBeFalsy();
    expect(Utils.emptyStr(false)).toBeFalsy();
    expect(Utils.emptyStr(0)).toBeFalsy();
  });

});
