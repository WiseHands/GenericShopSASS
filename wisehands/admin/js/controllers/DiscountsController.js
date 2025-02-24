angular.module('WiseHands')
    .controller('DiscountsController', ['$scope', '$http', 'signout', 'sideNavInit', 'shared',
        function ($scope, $http, signout, sideNavInit, shared) {
            $scope.loading = true;

            $http({
                method: 'GET',
                url: '/payment/detail',
            })
                .then(function successCallback(response) {
                    $scope.payment = response.data;
                    if ($scope.payment.minimumPayment === 0){
                        $scope.payment.minimumPayment = "";
                    }
                    $scope.loading = false;
                }, function errorCallback(data) {
                    console.log(data);
                    $scope.loading = false;
                    // signout.signOut();
                });

            $http({
                method: 'GET',
                url: '/coupons',
            })
                .then(function successCallback(response) {
                    $scope.coupons = response.data;
                    $scope.loading = false;
                }, function errorCallback(response) {
                    $scope.loading = false;
                    console.log(response);
                });


            $scope.setPaymentOptions = function () {
                let minimumPayment = document.getElementById('filled-in-minimumPayment').value;
                if (minimumPayment === ""){
                    $scope.payment.minimumPayment = 0;
                }
                console.log("$scope.payment.minimumPayment", $scope.payment.minimumPayment);

                $scope.loading = true;
                console.log("$scope.payment before sending", JSON.stringify($scope.payment));
                $http({
                    method: 'PUT',
                    url: '/payment/update',
                    data: $scope.payment,
                })
                    .then(function successCallback(response) {
                        $scope.payment = response.data;
                        if ($scope.payment.minimumPayment === 0){
                            $scope.payment.minimumPayment = "";
                        }

                        $scope.loading = false;
                        console.log("$scope.payment response", JSON.stringify($scope.payment));
                        showInfoMsg("SAVED");

                    }, function errorCallback(response) {
                        $scope.loading = false;
                        console.log(response);
                        showWarningMsg("ERROR");
                    });

            };
            $scope.createCoupons = function () {
                $scope.loading = true;
                $scope.couponRows.push($scope.plans);
                var coupons = {
                    plans: $scope.couponRows,
                    coupons: $scope.couponsIds
                };
                $http({
                    method: 'POST',
                    url: '/coupons',
                    data: coupons,
                })
                    .then(function successCallback(response) {
                        $scope.coupons = response.data;
                        $scope.result = {};
                        $scope.couponRows = [];
                        $scope.couponsIds = [];
                        $scope.plans = {};
                        $scope.loading = false;
                    }, function errorCallback(response) {
                        $scope.loading = false;
                        console.log(response);
                    });

            };
            $scope.setDiscountCard = function (coupon) {
                shared.setDiscountCards(coupon);
            };
            $scope.couponRows = [];
            $scope.createNewCouponRow = function () {
                $scope.couponRows.push({});
            };
            sideNavInit.sideNav();
        }]);
function showWarningMsg(msg) {
    toastr.clear();
    toastr.options = {
        "positionClass": "toast-bottom-right",
        "preventDuplicates": true
    };
    toastr.warning(msg);
}
function showInfoMsg(msg) {
    toastr.clear();
    toastr.options = {
        "positionClass": "toast-bottom-right",
        "preventDuplicates": true
    };
    toastr.info(msg);
}

