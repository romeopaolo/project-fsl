from flask import Flask, render_template
from statistics import get_statistics

app = Flask(__name__)


@app.route('/')
def show_home_page():
    return render_template('index.html')


# get statistics for player
@app.route('/statistics/<name>/<language>')
def retrieve_statistics(name, language):
    return get_statistics(name, language)


if __name__ == '__main__':
    app.run()
