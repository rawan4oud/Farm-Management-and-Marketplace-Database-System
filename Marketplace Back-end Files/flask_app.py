
# A very simple Flask Hello World app for you to get started with...

from flask import Flask, redirect, render_template, request, url_for
from flask_sqlalchemy import SQLAlchemy
from flask_login import login_required, login_user, LoginManager, logout_user, UserMixin
from werkzeug.security import check_password_hash, generate_password_hash

app = Flask(__name__)
app.config["DEBUG"] = True

SQLALCHEMY_DATABASE_URI = "mysql+mysqlconnector://{username}:{password}@{hostname}/{databasename}".format(
    username="selimh",
    password="COE4182021",
    hostname="selimh.mysql.pythonanywhere-services.com",
    databasename="selimh$FARM1",
)
app.config["SQLALCHEMY_DATABASE_URI"] = SQLALCHEMY_DATABASE_URI
app.config["SQLALCHEMY_POOL_RECYCLE"] = 299
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

db = SQLAlchemy(app)
app.secret_key = "COE4182021"
login_manager = LoginManager()
login_manager.init_app(app)


class Product(db.Model):
    __tablename__ = "product"
    productID = db.Column(db.Integer, primary_key=True);
    productType = db.Column(db.String)
    dateOfProd = db.Column(db.DateTime)
    dateOfExp = db.Column(db.DateTime)




class User(UserMixin, db.Model):

    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(128))
    password_hash = db.Column(db.String(128))

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)


    def get_id(self):
        return self.username


@login_manager.user_loader
def load_user(user_id):
    return User.query.filter_by(username=user_id).first()


@app.route("/")

def index():
    products_quantities=[]

    products_quantities.append(db.session.query(Product).filter(Product.productType == "Corn").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Wheat").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Rice").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Carrots").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Tomatoes").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Potatoes").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Beans").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Milk").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Meat").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Eggs").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Pumpkin").count())
    products_quantities.append(db.session.query(Product).filter(Product.productType == "Lettuce").count())

   # corn = db.session.query(Product).filter(Product.productType == "Corn").count()
   # wheat = db.session.query(Product).filter(Product.productType == "Wheat").count()
  #  rice = db.session.query(Product).filter(Product.productType == "Rice").count()
 #   carrots = db.session.query(Product).filter(Product.productType == "Carrots").count()
 #   tomatoes = db.session.query(Product).filter(Product.productType == "Tomatoes").count()
 #   potatoes = db.session.query(Product).filter(Product.productType == "Potatoes").count()
 #   beans = db.session.query(Product).filter(Product.productType == "Beans").count()
 #   milk = db.session.query(Product).filter(Product.productType == "Milk").count()
 #   meat = db.session.query(Product).filter(Product.productType == "Meat").count()
 #   eggs = db.session.query(Product).filter(Product.productType == "Eggs").count()
  #  pumpkin = db.session.query(Product).filter(Product.productType == "Pumpkin").count()
 #   lettuce = db.session.query(Product).filter(Product.productType == "Lettuce").count()
    db.session.commit()
    # omitting this will result in an error that i will explain
    return render_template("main_page.html",products_quantities=products_quantities  )



@app.route("/login/", methods=["GET", "POST"])
def login():
    if request.method == "GET":
        return render_template("login_page.html", error=False)

    user = load_user(request.form["username"])
    if user is None:
        return render_template("login_page.html", error=True)

    if not user.check_password(request.form["password"]):
        return render_template("login_page.html", error=True)

    login_user(user)
    return redirect(url_for('index'))



@app.route("/logout/")
@login_required
def logout():
    logout_user()
    return redirect(url_for('index'))