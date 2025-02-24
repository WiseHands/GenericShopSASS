angular.module('WiseHands')
    .controller('TransactionsController', ['$scope', '$http', 'signout', function ($scope, $http, signout) {
        $scope.loading = true;
        $http({
            method: 'GET',
            url: '/balance',
        })
            .then(function successCallback(response) {
                $scope.loading = false;
                $scope.balanceDetails = response.data.balanceTransactions;
            }, function errorCallback(data) {
                $scope.loading = false;
                // signout.signOut();
            });
        $scope.dateFormat = function (balanceDetail) {
                var date = new Date(balanceDetail.date);
                var ddyymm = new Date(balanceDetail.date).toISOString().slice(0,10);
                var hour = (date.getHours()<10?'0':'') + date.getHours();
                var minute = (date.getMinutes()<10?'0':'') + date.getMinutes();
                return ddyymm + ' ' + hour + ':' + minute;
        };
        $scope.paymentState = function(balanceDetail){
            if (balanceDetail.amount > 0){
                return 'teal';
            } else {
                return '#03a9f4';
            }
        };
        $scope.isTransactionPayed = function (balanceDetail) {
            if (balanceDetail.state === 'PAYED') {
                return true;
            }
            
        }
    }]);
