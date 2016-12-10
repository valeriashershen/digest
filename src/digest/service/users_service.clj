(ns digest.service.users-service
  (:require [digest.dal.users-provider :as user]))

(def userdal (user/->users-provider))

(defn sign-in [{{:keys [email password] :as user} :params session :session} error success]
  (let [{id :id stored-pass :password admin :is_admin} (.get-item userdal {:email email})]
    (if (and stored-pass (= password stored-pass))
      (if admin
        (-> (success) (assoc :session (assoc session :user_id id :email email :admin true)))
        (-> (success) (assoc :session (assoc session :user_id id :email email :admin false))))
      (error))))

(defn sign-up [{{:keys [email password] :as user} :params} success error]
  (if-not (.get-item userdal {:email email})
    (do
      (.create-item userdal (merge user {:is_admin false}))
      (success))
    (error)))


(defn logout [{session :session} success]
  (-> (success) (assoc :session (assoc session :admin nil :user_id nil :username nil))))
