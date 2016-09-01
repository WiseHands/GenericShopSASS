(function () {
    angular.module('WiseShop', [
            'ngRoute', 'angularSpinners'
        ])
        .config(['$routeProvider',
            function ($routeProvider) {

                $routeProvider.
                when('/',{
                    templateUrl:'wisehands/shop/partials/shopView.html',
                    controller:'ShopController'
                }).
                when('/contacts',{
                    templateUrl:'wisehands/shop/partials/contacts.html',
                    controller:'ContactsController'
                }).
                otherwise({
                    redirectTo:'/'
                });
            }])
})();
