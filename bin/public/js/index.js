$(function() {
  var table = $(".data-table").DataTable({
    pageLength: 10,
    ajax: "http://localhost:8080/poll-payments",
    columns: [
      { data: "blockNo" },
      { data: "txnId" },
      { data: "accountNumber" },
      { data: "bankName" },
      { data: "currency" },
      { data: "amount" }
    ],
    "order": [[ 0, "desc" ]],
    createdRow: function(row, data, dataIndex) {
      if (data.status == 1) {
        $(row).addClass("light-red-row-color");
      } else if (data.status == 2) {
        $(row).addClass("light-orange-row-color");
      }
    }
  });

  setInterval(function() {
    table.ajax.reload();
  }, 10000);

  $("#accountForm").submit(function(e) {
    e.preventDefault();

    $(".success-msg").hide();
    $(".error-msg").hide();

    var jsonData = $("#accountForm").serializeJSON();
    var stringifiedData = JSON.stringify(jsonData);

    axios
      .post("http://localhost:8080/new-payment", stringifiedData)
      .then(function(response) {
        $(".success-msg").show();
        $("#accountForm").trigger("reset");
      })
      .catch(function(error) {
        console.log("Error:", data);
        $(".error-msg").show();
      });
  });

  $("#clearBtn").click(function(e) {
    $("#accountForm").trigger("reset");
  });
});
